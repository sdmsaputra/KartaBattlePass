package com.minekarta.kartabattlepass.service;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.leaderboard.LeaderboardEntry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardService {

    private final KartaBattlePass plugin;
    private List<LeaderboardEntry> sortedLeaderboard;

    public LeaderboardService(KartaBattlePass plugin) {
        this.plugin = plugin;
        this.sortedLeaderboard = new ArrayList<>();
    }

    public void updateLeaderboard() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            File dataFile = new File(new File(plugin.getDataFolder(), "data"), "players.yml");
            if (!dataFile.exists()) {
                sortedLeaderboard = Collections.emptyList();
                return;
            }

            FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
            List<LeaderboardEntry> entries = new ArrayList<>();

            ConfigurationSection playersSection = dataConfig.getConfigurationSection("");
            if (playersSection == null) {
                sortedLeaderboard = Collections.emptyList();
                return;
            }

            for (String uuid : playersSection.getKeys(false)) {
                ConfigurationSection playerSection = playersSection.getConfigurationSection(uuid);
                if (playerSection != null) {
                    String name = playerSection.getString("name", "Unknown");
                    int level = playerSection.getInt("level", 1);
                    int exp = playerSection.getInt("exp", 0);
                    // Rank will be assigned after sorting
                    entries.add(new LeaderboardEntry(name, level, exp, 0));
                }
            }

            // Sort by level descending, then by exp descending
            entries.sort(Comparator.comparingInt(LeaderboardEntry::getLevel).reversed()
                    .thenComparing(LeaderboardEntry::getExp, Comparator.reverseOrder()));

            // Assign ranks
            List<LeaderboardEntry> rankedEntries = new ArrayList<>();
            for (int i = 0; i < entries.size(); i++) {
                LeaderboardEntry oldEntry = entries.get(i);
                rankedEntries.add(new LeaderboardEntry(oldEntry.getPlayerName(), oldEntry.getLevel(), oldEntry.getExp(), i + 1));
            }

            this.sortedLeaderboard = rankedEntries;
            plugin.getLogger().info("Leaderboard updated with " + rankedEntries.size() + " players.");
        });
    }

    public List<LeaderboardEntry> getLeaderboard() {
        // Return a copy to prevent modification
        return new ArrayList<>(sortedLeaderboard);
    }
}
