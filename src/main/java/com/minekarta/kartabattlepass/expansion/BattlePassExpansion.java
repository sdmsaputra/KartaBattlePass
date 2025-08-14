package com.minekarta.kartabattlepass.expansion;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import com.minekarta.kartabattlepass.reward.Reward;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BattlePassExpansion extends PlaceholderExpansion {

    private final KartaBattlePass plugin;

    public BattlePassExpansion(KartaBattlePass plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "kartabattlepass";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer == null || !offlinePlayer.isOnline()) {
            return "";
        }

        Player player = offlinePlayer.getPlayer();
        BattlePassPlayer bpp = plugin.getBattlePassStorage().getBattlePassPlayer(player.getUniqueId());
        if (bpp == null) {
            return "";
        }

        if (params.equalsIgnoreCase("unclaimed_rewards")) {
            return String.valueOf(countUnclaimedRewards(bpp, player));
        }

        if (params.equalsIgnoreCase("next_reward_level")) {
            return getNextRewardLevel(bpp);
        }

        return null;
    }

    private int countUnclaimedRewards(BattlePassPlayer bpp, Player player) {
        int unclaimedCount = 0;
        int maxLevel = plugin.getConfig().getInt("battlepass.max-level", 100);

        for (int level = 1; level <= bpp.getLevel(); level++) {
            if (level > maxLevel) break;

            List<Reward> rewards = plugin.getRewardService().getRewardsForLevel(level);
            int rewardIndex = 0;
            for (Reward reward : rewards) {
                String rewardId = level + ":" + rewardIndex;
                if (!bpp.hasClaimedReward(level, rewardId)) {
                    if (reward.isPremium() && !player.hasPermission("kartabattlepass.premium")) {
                        // Skip premium rewards if player doesn't have access
                    } else {
                        unclaimedCount++;
                    }
                }
                rewardIndex++;
            }
        }
        return unclaimedCount;
    }

    private String getNextRewardLevel(BattlePassPlayer bpp) {
        int maxLevel = plugin.getConfig().getInt("battlepass.max-level", 100);
        for (int level = bpp.getLevel() + 1; level <= maxLevel; level++) {
            if (!plugin.getRewardService().getRewardsForLevel(level).isEmpty()) {
                return String.valueOf(level);
            }
        }
        return "Max Level";
    }
}
