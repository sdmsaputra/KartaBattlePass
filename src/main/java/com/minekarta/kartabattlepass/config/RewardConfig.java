package com.minekarta.kartabattlepass.config;

import com.minekarta.kartabattlepass.KartaBattlePass;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RewardConfig {
    private final KartaBattlePass plugin;
    private FileConfiguration rewardConfig = null;
    private File rewardConfigFile = null;

    public RewardConfig(KartaBattlePass plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (rewardConfigFile == null) {
            rewardConfigFile = new File(plugin.getDataFolder(), "rewards.yml");
        }
        rewardConfig = YamlConfiguration.loadConfiguration(rewardConfigFile);

        InputStream defConfigStream = plugin.getResource("rewards.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            rewardConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (rewardConfig == null) {
            reloadConfig();
        }
        return rewardConfig;
    }

    public void saveDefaultConfig() {
        if (rewardConfigFile == null) {
            rewardConfigFile = new File(plugin.getDataFolder(), "rewards.yml");
        }
        if (!rewardConfigFile.exists()) {
            plugin.saveResource("rewards.yml", false);
        }
    }
}
