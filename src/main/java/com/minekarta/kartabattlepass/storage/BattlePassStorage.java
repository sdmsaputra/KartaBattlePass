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
        this.maxLevel = config.getInt("battlepass.max-level", 100);
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

                Map<Integer, List<String>> claimedRewards = new ConcurrentHashMap<>();
                if (section.isConfigurationSection("claimedRewards")) {
                    ConfigurationSection rewardsSection = section.getConfigurationSection("claimedRewards");
                    for (String levelKey : rewardsSection.getKeys(false)) {
                        claimedRewards.put(Integer.parseInt(levelKey), rewardsSection.getStringList(levelKey));
                    }
                }

                List<String> completedMissions = section.getStringList("completedMissions");

                bpPlayer = new BattlePassPlayer(uuid, name, level, exp, claimedRewards, completedMissions);
            } else {
                // Create new player data
                bpPlayer = new BattlePassPlayer(uuid, player.getName(), 1, 0, new ConcurrentHashMap<>(), new ArrayList<>());
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

    // All XP and level up logic will be moved to a dedicated ExperienceService.
    // This class should only be responsible for storage.
}
