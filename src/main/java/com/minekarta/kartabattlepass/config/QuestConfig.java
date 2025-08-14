package com.minekarta.kartabattlepass.config;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class QuestConfig {

    private final KartaBattlePass plugin;
    private File configFile;
    private FileConfiguration config;

    private final Map<String, Quest> quests = new HashMap<>();

    public QuestConfig(KartaBattlePass plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "quests.yml");
        saveDefaultConfig();
        loadConfig();
    }

    private void saveDefaultConfig() {
        if (!configFile.exists()) {
            plugin.saveResource("quests.yml", false);
        }
    }

    public void loadConfig() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
        loadQuests();
    }

    private void loadQuests() {
        quests.clear();
        ConfigurationSection questsSection = config.getConfigurationSection("quests");
        if (questsSection == null) {
            plugin.getLogger().warning("Could not find 'quests' section in quests.yml");
            return;
        }

        for (String questId : questsSection.getKeys(false)) {
            ConfigurationSection questSection = questsSection.getConfigurationSection(questId);
            if (questSection != null) {
                String type = questSection.getString("type");
                String target = questSection.getString("target", null); // Optional
                int amount = questSection.getInt("amount", 1);
                List<String> rewards = questSection.getStringList("rewards");

                if (type == null) {
                    plugin.getLogger().warning("Quest '" + questId + "' is missing a 'type'. Skipping.");
                    continue;
                }

                Quest quest = new Quest(questId, type, target, amount, rewards);
                quests.put(questId, quest);
            }
        }
        plugin.getLogger().info("Loaded " + quests.size() + " quests.");
    }

    public Map<String, Quest> getQuests() {
        return Collections.unmodifiableMap(quests);
    }

    public Quest getQuest(String questId) {
        return quests.get(questId);
    }
}
