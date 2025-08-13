package com.minekarta.kartabattlepass.storage;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class BattlePassStorage {

    private final KartaBattlePass plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, BattlePassPlayer> playerData = new HashMap<>();

    private int maxLevel;
    private int expPerLevel;

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
        this.expPerLevel = config.getInt("battlepass.expPerLevel", 1000);
    }

    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        String path = uuid.toString();

        // Refresh the data from the file before loading
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        if (dataConfig.isConfigurationSection(path)) {
            ConfigurationSection section = dataConfig.getConfigurationSection(path);
            String name = section.getString("name", player.getName());
            int level = section.getInt("level", 1);
            int exp = section.getInt("exp", 0);
            List<Integer> claimedRewards = section.getIntegerList("claimedRewards");

            BattlePassPlayer bpPlayer = new BattlePassPlayer(uuid, name, level, exp, claimedRewards);
            playerData.put(uuid, bpPlayer);
        } else {
            // Create new player data
            BattlePassPlayer bpPlayer = new BattlePassPlayer(uuid, player.getName(), 1, 0, new ArrayList<>());
            playerData.put(uuid, bpPlayer);
        }
        plugin.getLogger().info("Loaded data for player " + player.getName());
    }

    public void savePlayerData(UUID uuid, boolean saveToFile) {
        BattlePassPlayer bpPlayer = playerData.get(uuid);
        if (bpPlayer == null) {
            return; // No data to save
        }

        String path = uuid.toString();
        dataConfig.set(path + ".name", bpPlayer.getName());
        dataConfig.set(path + ".level", bpPlayer.getLevel());
        dataConfig.set(path + ".exp", bpPlayer.getExp());
        dataConfig.set(path + ".claimedRewards", bpPlayer.getClaimedRewards());

        if (saveToFile) {
            try {
                dataConfig.save(dataFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not save player data for " + uuid, e);
            }
        }
    }

    public void unloadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        savePlayerData(uuid, true);
        playerData.remove(uuid);
        plugin.getLogger().info("Saved and unloaded data for player " + player.getName());
    }

    public void saveAllPlayerData() {
        if (playerData.isEmpty()) {
            return;
        }

        for (UUID uuid : playerData.keySet()) {
            savePlayerData(uuid, false); // Save to config object without writing to file each time
        }

        try {
            dataConfig.save(dataFile);
            plugin.getLogger().info("Successfully saved all player data.");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save all player data to players.yml!", e);
        }
    }

    public BattlePassPlayer getBattlePassPlayer(UUID uuid) {
        return playerData.get(uuid);
    }

    public void addExp(UUID uuid, int amount) {
        BattlePassPlayer bpPlayer = getBattlePassPlayer(uuid);
        if (bpPlayer == null || bpPlayer.getLevel() >= maxLevel) {
            return; // Player not found or is at max level
        }

        bpPlayer.setExp(bpPlayer.getExp() + amount);

        while (bpPlayer.getExp() >= expPerLevel && bpPlayer.getLevel() < maxLevel) {
            bpPlayer.setExp(bpPlayer.getExp() - expPerLevel);
            bpPlayer.setLevel(bpPlayer.getLevel() + 1);

            plugin.getLogger().info("Player " + bpPlayer.getName() + " leveled up to level " + bpPlayer.getLevel() + "!");
            dispatchReward(uuid, bpPlayer.getLevel());
        }
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
