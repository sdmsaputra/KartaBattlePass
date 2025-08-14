package com.minekarta.kartabattlepass.service;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import com.minekarta.kartabattlepass.reward.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class RewardService {

    private final KartaBattlePass plugin;
    private final Map<Integer, List<Reward>> rewardsByLevel = new HashMap<>();

    public RewardService(KartaBattlePass plugin) {
        this.plugin = plugin;
        loadRewards();
    }

    public void loadRewards() {
        rewardsByLevel.clear();
        FileConfiguration config = plugin.getRewardConfig().getConfig();
        ConfigurationSection rewardsSection = config.getConfigurationSection("rewards");
        if (rewardsSection == null) {
            plugin.getLogger().warning("No 'rewards' section found in rewards.yml.");
            return;
        }

        for (String levelKey : rewardsSection.getKeys(false)) {
            try {
                int level = Integer.parseInt(levelKey);
                List<Map<?, ?>> rewardMaps = rewardsSection.getMapList(levelKey);
                List<Reward> levelRewards = new ArrayList<>();
                int rewardIndex = 0;

                for (Map<?, ?> rewardMap : rewardMaps) {
                    String type = (String) rewardMap.get("type");
                    if (type == null) continue;

                    String rewardId = level + ":" + rewardIndex;
                    Reward reward = createRewardFromMap(rewardMap, level, rewardId);
                    if (reward != null) {
                        levelRewards.add(reward);
                    }
                    rewardIndex++;
                }
                rewardsByLevel.put(level, levelRewards);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid level format in rewards.yml: " + levelKey);
            }
        }
        plugin.getLogger().info("Loaded rewards for " + rewardsByLevel.size() + " levels.");
    }

    private Reward createRewardFromMap(Map<?, ?> map, int level, String rewardId) {
        String type = (String) map.get("type");
        String track = (String) map.get("track");

        switch (type.toLowerCase()) {
            case "item":
                return createItemReward(map, track, level, rewardId);
            case "command":
                return createCommandReward(map, track, level, rewardId);
            case "money":
                return createMoneyReward(map, track, level, rewardId);
            case "permission":
                return createPermissionReward(map, track, level, rewardId);
            default:
                plugin.getLogger().warning("Unknown reward type in rewards.yml: " + type);
                return null;
        }
    }

    private ItemReward createItemReward(Map<?, ?> map, String track, int level, String rewardId) {
        try {
            Material material = Material.valueOf(((String) map.get("material")).toUpperCase());
            int amount = map.get("amount") != null ? (int) map.get("amount") : 1;
            String name = (String) map.get("name");
            List<String> lore = (List<String>) map.get("lore");
            List<String> enchantments = (List<String>) map.get("enchantments");
            return new ItemReward(track, level, rewardId, material, amount, name, lore, enchantments);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to parse item reward: " + e.getMessage());
            return null;
        }
    }

    private CommandReward createCommandReward(Map<?, ?> map, String track, int level, String rewardId) {
        String command = (String) map.get("command");
        String executor = (String) map.get("executor");
        return new CommandReward(track, level, rewardId, command, executor);
    }

    private MoneyReward createMoneyReward(Map<?, ?> map, String track, int level, String rewardId) {
        double amount = ((Number) map.get("amount")).doubleValue();
        return new MoneyReward(track, level, rewardId, amount);
    }

    private PermissionReward createPermissionReward(Map<?, ?> map, String track, int level, String rewardId) {
        String permission = (String) map.get("permission");
        String duration = (String) map.get("duration");
        return new PermissionReward(track, level, rewardId, permission, duration);
    }

    public List<Reward> getRewardsForLevel(int level) {
        return rewardsByLevel.getOrDefault(level, Collections.emptyList());
    }

    public List<Reward> getAllRewards() {
        return rewardsByLevel.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public void claimLevelRewards(Player player, int level) {
        BattlePassPlayer bpp = plugin.getBattlePassStorage().getPlayerData(player.getUniqueId());
        if (bpp == null) {
            player.sendMessage(ChatColor.RED + "Your Battle Pass data was not found.");
            return;
        }

        if (bpp.getLevel() < level) {
            player.sendMessage(ChatColor.RED + "You have not reached level " + level + " yet.");
            return;
        }

        List<Reward> rewards = getRewardsForLevel(level);
        if (rewards.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No rewards are configured for level " + level + ".");
            return;
        }

        boolean claimedSomething = false;
        for (Reward reward : rewards) {
            // Check if already claimed
            if (bpp.hasClaimedReward(reward.getLevel(), reward.getRewardId())) {
                continue;
            }

            // Check for premium track
            if (reward.isPremium() && !player.hasPermission("kartabattlepass.premium")) {
                continue;
            }

            reward.give(player);
            bpp.addClaimedReward(reward.getLevel(), reward.getRewardId());
            claimedSomething = true;
        }

        if (claimedSomething) {
            player.sendMessage(ChatColor.GREEN + "You have successfully claimed rewards for level " + level + "!");
            plugin.getBattlePassStorage().savePlayerData(player.getUniqueId(), true);
        } else {
            player.sendMessage(ChatColor.YELLOW + "All available rewards for level " + level + " have already been claimed.");
        }
    }
}
