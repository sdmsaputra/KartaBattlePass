package com.minekarta.kartabattlepass.storage;

import com.minekarta.kartabattlepass.KartaBattlePass;
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

public class BattlePassStorage {

    private final KartaBattlePass plugin;
    private final File playersFile;
    private FileConfiguration playersConfig;
    private final Map<UUID, BattlePassPlayer> playersCache = new ConcurrentHashMap<>();

    public BattlePassStorage(KartaBattlePass plugin) {
        this.plugin = plugin;
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) {
                plugin.getLogger().severe("Could not create data folder!");
            }
        }
        this.playersFile = new File(dataFolder, "players.yml");
        if (!playersFile.exists()) {
            try {
                if (!playersFile.createNewFile()) {
                    plugin.getLogger().severe("Could not create players.yml file!");
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create players.yml file!");
                e.printStackTrace();
            }
        }
        this.playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    }

    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        if (playersCache.containsKey(uuid)) {
            return; // Already loaded
        }

        ConfigurationSection playerSection = playersConfig.getConfigurationSection(uuid.toString());
        if (playerSection == null) {
            // New player, create a default profile
            BattlePassPlayer newPlayer = new BattlePassPlayer(uuid, player.getName(), 1, 0, new ArrayList<>());
            playersCache.put(uuid, newPlayer);
            plugin.getLogger().info("Created new Battle Pass profile for " + player.getName());
        } else {
            // Existing player
            String name = playerSection.getString("name", player.getName());
            int level = playerSection.getInt("level", 1);
            int exp = playerSection.getInt("exp", 0);
            List<Integer> claimedRewards = playerSection.getIntegerList("claimedRewards");
            BattlePassPlayer battlePassPlayer = new BattlePassPlayer(uuid, name, level, exp, claimedRewards);
            playersCache.put(uuid, battlePassPlayer);
        }
    }

    public void savePlayerData(Player player, boolean removeFromCache) {
        UUID uuid = player.getUniqueId();
        BattlePassPlayer battlePassPlayer = playersCache.get(uuid);
        if (battlePassPlayer == null) {
            return; // No data to save
        }

        // Ensure player's name is up-to-date
        battlePassPlayer.setName(player.getName());

        String playerUuidString = uuid.toString();
        playersConfig.set(playerUuidString + ".name", battlePassPlayer.getName());
        playersConfig.set(playerUuidString + ".level", battlePassPlayer.getLevel());
        playersConfig.set(playerUuidString + ".exp", battlePassPlayer.getExp());
        playersConfig.set(playerUuidString + ".claimedRewards", battlePassPlayer.getClaimedRewards());

        saveFile(); // Save immediately on single-player save events

        if (removeFromCache) {
            playersCache.remove(uuid);
        }
    }

    public void saveAllPlayerData() {
        if (playersCache.isEmpty()) {
            return;
        }

        plugin.getLogger().info("Saving all Battle Pass data...");
        for (BattlePassPlayer battlePassPlayer : playersCache.values()) {
            String playerUuidString = battlePassPlayer.getUuid().toString();
            playersConfig.set(playerUuidString + ".name", battlePassPlayer.getName());
            playersConfig.set(playerUuidString + ".level", battlePassPlayer.getLevel());
            playersConfig.set(playerUuidString + ".exp", battlePassPlayer.getExp());
            playersConfig.set(playerUuidString + ".claimedRewards", battlePassPlayer.getClaimedRewards());
        }
        saveFile();
        plugin.getLogger().info("All Battle Pass data saved.");
    }

    public void saveFile() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save to players.yml file!");
            e.printStackTrace();
        }
    }

    public BattlePassPlayer getBattlePassPlayer(UUID uuid) {
        return playersCache.get(uuid);
    }
}
