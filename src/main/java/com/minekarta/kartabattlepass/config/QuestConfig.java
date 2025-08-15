package com.minekarta.kartabattlepass.config;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.quest.Quest;
import com.minekarta.kartabattlepass.quest.QuestCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class QuestConfig {

    private final KartaBattlePass plugin;
    private File configFile;
    private FileConfiguration config;

    private final Map<String, Quest> quests = new HashMap<>();
    private final Map<String, QuestCategory> questCategories = new LinkedHashMap<>();

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
        loadQuestCategories();
    }

    private void loadQuestCategories() {
        questCategories.clear();
        ConfigurationSection categoriesSection = config.getConfigurationSection("quest-categories");
        if (categoriesSection == null) {
            plugin.getLogger().warning("Could not find 'quest-categories' section in quests.yml");
            return;
        }

        for (String categoryId : categoriesSection.getKeys(false)) {
            ConfigurationSection categorySection = categoriesSection.getConfigurationSection(categoryId);
            if (categorySection != null) {
                String displayName = categorySection.getString("display-name", "Unnamed Category");
                String displayItem = categorySection.getString("display-item", "STONE");
                List<String> questIds = categorySection.getStringList("quests");

                if (questIds.isEmpty()) {
                    plugin.getLogger().warning("Quest category '" + categoryId + "' has no quests listed. Skipping.");
                    continue;
                }

                QuestCategory category = new QuestCategory(categoryId, displayName, displayItem, questIds);
                questCategories.put(categoryId, category);
            }
        }
        plugin.getLogger().info("Loaded " + questCategories.size() + " quest categories.");
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
                String displayName = questSection.getString("display-name", "Unnamed Quest");
                int amount = questSection.getInt("amount", 1);
                int exp = questSection.getInt("exp", 0);
                List<String> rewards = questSection.getStringList("rewards");

                if (type == null) {
                    plugin.getLogger().warning("Quest '" + questId + "' is missing a 'type'. Skipping.");
                    continue;
                }

                Quest quest = new Quest(questId, type, target, displayName, amount, exp, rewards);
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

    public Map<String, QuestCategory> getQuestCategories() {
        return Collections.unmodifiableMap(questCategories);
    }
}
