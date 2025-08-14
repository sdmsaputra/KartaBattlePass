package com.minekarta.kartabattlepass.storage;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import com.minekarta.kartabattlepass.quest.PlayerQuestProgress;
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

                Map<String, PlayerQuestProgress> questProgress = new ConcurrentHashMap<>();
                if (section.isConfigurationSection("questProgress")) {
                    ConfigurationSection questSection = section.getConfigurationSection("questProgress");
                    for (String questId : questSection.getKeys(false)) {
                        PlayerQuestProgress progress = new PlayerQuestProgress(questId);
                        progress.setCurrentAmount(questSection.getInt(questId + ".currentAmount", 0));
                        progress.setCompleted(questSection.getBoolean(questId + ".completed", false));
                        questProgress.put(questId, progress);
                    }
                }

                bpPlayer = new BattlePassPlayer(uuid, name, level, exp, claimedRewards, questProgress);
            } else {
                // Create new player data
                bpPlayer = new BattlePassPlayer(uuid, player.getName(), 1, 0, new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
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

                // Save quest progress
                for (Map.Entry<String, PlayerQuestProgress> entry : bpPlayer.getQuestProgress().entrySet()) {
                    String questId = entry.getKey();
                    PlayerQuestProgress progress = entry.getValue();
                    String questPath = path + ".questProgress." + questId;
                    currentData.set(questPath + ".currentAmount", progress.getCurrentAmount());
                    currentData.set(questPath + ".completed", progress.isCompleted());
                }

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

                    // Save quest progress for all players
                    for (Map.Entry<String, PlayerQuestProgress> questEntry : bpPlayer.getQuestProgress().entrySet()) {
                        String questId = questEntry.getKey();
                        PlayerQuestProgress progress = questEntry.getValue();
                        String questPath = path + ".questProgress." + questId;
                        currentData.set(questPath + ".currentAmount", progress.getCurrentAmount());
                        currentData.set(questPath + ".completed", progress.isCompleted());
                    }
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

    public BattlePassPlayer getPlayerData(UUID uniqueId) {
        return playerData.get(uniqueId);
    }

    public List<BattlePassPlayer> getAllPlayerData() {
        List<BattlePassPlayer> allPlayers = new ArrayList<>();
        FileConfiguration fileData = YamlConfiguration.loadConfiguration(dataFile);

        ConfigurationSection playersSection = fileData.getConfigurationSection("");
        if (playersSection == null) return allPlayers;

        for (String uuidString : playersSection.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                ConfigurationSection section = playersSection.getConfigurationSection(uuidString);
                if (section == null) continue;

                // Check for online player data first to get the most up-to-date info
                if (playerData.containsKey(uuid)) {
                    allPlayers.add(playerData.get(uuid));
                    continue;
                }

                String name = section.getString("name");
                int level = section.getInt("level", 1);
                int exp = section.getInt("exp", 0);

                Map<Integer, List<String>> claimedRewards = new ConcurrentHashMap<>();
                if (section.isConfigurationSection("claimedRewards")) {
                    ConfigurationSection rewardsSection = section.getConfigurationSection("claimedRewards");
                    for (String levelKey : rewardsSection.getKeys(false)) {
                        claimedRewards.put(Integer.parseInt(levelKey), rewardsSection.getStringList(levelKey));
                    }
                }

                Map<String, PlayerQuestProgress> questProgress = new ConcurrentHashMap<>();
                if (section.isConfigurationSection("questProgress")) {
                    ConfigurationSection questSection = section.getConfigurationSection("questProgress");
                    for (String questId : questSection.getKeys(false)) {
                        PlayerQuestProgress progress = new PlayerQuestProgress(questId);
                        progress.setCurrentAmount(questSection.getInt(questId + ".currentAmount", 0));
                        progress.setCompleted(questSection.getBoolean(questId + ".completed", false));
                        questProgress.put(questId, progress);
                    }
                }

                allPlayers.add(new BattlePassPlayer(uuid, name, level, exp, claimedRewards, questProgress));

            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Skipping invalid UUID in players.yml: " + uuidString);
            }
        }

        return allPlayers;
    }
}
