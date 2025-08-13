package com.minekarta.kartabattlepass.storage;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.event.PlayerBattlePassLevelUpEvent;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class BattlePassStorage {

    private final KartaBattlePass plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, BattlePassPlayer> playerData = new ConcurrentHashMap<>();
    private final Object saveLock = new Object();

    private int maxLevel;

    public BattlePassStorage(KartaBattlePass plugin) {
        this.plugin = plugin;
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.dataFile = new File(dataFolder, "players.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create players.yml!", e);
            }
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadConfigValues();
    }

    public void loadConfigValues() {
        // Reload config from disk to get latest values
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        this.maxLevel = config.getInt("battlepass.maxLevel", 50);
    }

    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        String path = uuid.toString();

        // Run asynchronously to avoid blocking the main thread for file I/O
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // Refresh the data from the file before loading
            FileConfiguration fileData = YamlConfiguration.loadConfiguration(dataFile);
            BattlePassPlayer bpPlayer;

            if (fileData.isConfigurationSection(path)) {
                ConfigurationSection section = fileData.getConfigurationSection(path);
                String name = section.getString("name", player.getName());
                int level = section.getInt("level", 1);
                int exp = section.getInt("exp", 0);
                List<Integer> claimedRewards = section.getIntegerList("claimedRewards");
                List<String> completedMissions = section.getStringList("completedMissions");

                bpPlayer = new BattlePassPlayer(uuid, name, level, exp, claimedRewards, completedMissions);
            } else {
                // Create new player data
                bpPlayer = new BattlePassPlayer(uuid, player.getName(), 1, 0, new ArrayList<>(), new ArrayList<>());
            }

            // Put the loaded data into the cache on the main thread
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                playerData.put(uuid, bpPlayer);
                plugin.getLogger().info("Loaded data for player " + player.getName());
            });
        });
    }

    public void savePlayerData(UUID uuid, boolean async) {
        BattlePassPlayer bpPlayer = playerData.get(uuid);
        if (bpPlayer == null) {
            return; // No data to save
        }

        Runnable saveTask = () -> {
            synchronized (saveLock) {
                // Load the latest data from the file before saving to avoid overwriting changes
                FileConfiguration currentData = YamlConfiguration.loadConfiguration(dataFile);

                String path = uuid.toString();
                currentData.set(path + ".name", bpPlayer.getName());
                currentData.set(path + ".level", bpPlayer.getLevel());
                currentData.set(path + ".exp", bpPlayer.getExp());
                currentData.set(path + ".claimedRewards", bpPlayer.getClaimedRewards());
                currentData.set(path + ".completedMissions", bpPlayer.getCompletedMissions());

                try {
                    currentData.save(dataFile);
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, "Could not save player data for " + uuid, e);
                }
            }
        };

        if (async) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, saveTask);
        } else {
            saveTask.run();
        }
    }

    public void unloadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        // Save data asynchronously on logout
        savePlayerData(uuid, true);
        playerData.remove(uuid);
        plugin.getLogger().info("Saved and unloaded data for player " + player.getName());
    }

    public void saveAllPlayerData() {
        if (playerData.isEmpty()) {
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            synchronized (saveLock) {
                FileConfiguration currentData = YamlConfiguration.loadConfiguration(dataFile);

                for (Map.Entry<UUID, BattlePassPlayer> entry : playerData.entrySet()) {
                    UUID uuid = entry.getKey();
                    BattlePassPlayer bpPlayer = entry.getValue();
                    String path = uuid.toString();
                    currentData.set(path + ".name", bpPlayer.getName());
                    currentData.set(path + ".level", bpPlayer.getLevel());
                    currentData.set(path + ".exp", bpPlayer.getExp());
                    currentData.set(path + ".claimedRewards", bpPlayer.getClaimedRewards());
                    currentData.set(path + ".completedMissions", bpPlayer.getCompletedMissions());
                }

                try {
                    currentData.save(dataFile);
                    plugin.getLogger().info("Successfully saved all player data.");
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, "Could not save all player data to players.yml!", e);
                }
            }
        });
    }

    public BattlePassPlayer getBattlePassPlayer(UUID uuid) {
        return playerData.get(uuid);
    }

    // --- New Methods ---

    public int getLevel(Player player) {
        BattlePassPlayer bpPlayer = getBattlePassPlayer(player.getUniqueId());
        return (bpPlayer != null) ? bpPlayer.getLevel() : 1;
    }

    public int getXP(Player player) {
        BattlePassPlayer bpPlayer = getBattlePassPlayer(player.getUniqueId());
        return (bpPlayer != null) ? bpPlayer.getExp() : 0;
    }

    public int getXPNeeded(Player player) {
        BattlePassPlayer bpPlayer = getBattlePassPlayer(player.getUniqueId());
        if (bpPlayer == null || bpPlayer.getLevel() >= maxLevel) {
            return 0; // No XP needed if not loaded or at max level
        }
        int required = bpPlayer.getLevel() * 100;
        return Math.max(0, required - bpPlayer.getExp());
    }

    public void addXP(Player player, int amount) {
        BattlePassPlayer bpPlayer = getBattlePassPlayer(player.getUniqueId());
        if (bpPlayer == null || bpPlayer.getLevel() >= maxLevel) {
            return; // Player not found or is at max level
        }

        bpPlayer.setExp(bpPlayer.getExp() + amount);

        // Check for level up
        boolean leveledUp = false;
        int xpNeededForNextLevel = bpPlayer.getLevel() * 100;
        while (bpPlayer.getExp() >= xpNeededForNextLevel && bpPlayer.getLevel() < maxLevel) {
            leveledUp = true;
            int oldLevel = bpPlayer.getLevel();
            bpPlayer.setExp(bpPlayer.getExp() - xpNeededForNextLevel);
            bpPlayer.setLevel(oldLevel + 1);

            // Run on main thread to send message and call event
            final int newLevel = bpPlayer.getLevel();
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                // Send message
                String message = "&a&l[BattlePass] &fSelamat! Kamu naik ke Level &b" + newLevel + "&f!";
                player.sendMessage(plugin.getMiniMessage().deserialize(message));

                // Call event
                PlayerBattlePassLevelUpEvent event = new PlayerBattlePassLevelUpEvent(player, oldLevel, newLevel);
                plugin.getServer().getPluginManager().callEvent(event);

                // Dispatch any rewards
                dispatchReward(player.getUniqueId(), newLevel);
            });

            // Update xpNeeded for the new level
            xpNeededForNextLevel = bpPlayer.getLevel() * 100;
        }

        // Save progress, especially after leveling up
        savePlayerData(player.getUniqueId(), true);
    }

    private void dispatchReward(UUID playerUuid, int level) {
        String command = plugin.getConfig().getString("rewards." + level);
        if (command == null || command.isEmpty()) {
            return; // No reward for this level
        }

        Player player = plugin.getServer().getPlayer(playerUuid);
        if (player == null || !player.isOnline()) {
            // In a more complex system, you might queue rewards for offline players.
            // For now, we'll just log it and skip.
            plugin.getLogger().warning("Player " + playerUuid + " is offline, could not give level " + level + " reward.");
            return;
        }

        final String finalCommand = command.replace("%player%", player.getName());

        // Run on the main thread
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), finalCommand);
            plugin.getLogger().info("Dispatched reward for " + player.getName() + " (Level " + level + "): " + finalCommand);
        });
    }
}
