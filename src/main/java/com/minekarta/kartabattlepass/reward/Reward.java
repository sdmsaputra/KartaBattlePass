package com.minekarta.kartabattlepass.reward;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Reward {
    private final String track;
    private final int level;
    private final String rewardId;

    public Reward(String track, int level, String rewardId) {
        this.track = track != null ? track : "free";
        this.level = level;
        this.rewardId = rewardId;
    }

    public String getTrack() {
        return track;
    }

    public boolean isPremium() {
        return "premium".equalsIgnoreCase(track);
    }

    public int getLevel() {
        return level;
    }

    public String getRewardId() {
        return rewardId;
    }

    public abstract void give(Player player);

    public abstract ItemStack getDisplayItem();
}
