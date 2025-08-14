package com.minekarta.kartabattlepass.reward;

import org.bukkit.entity.Player;

public abstract class Reward {
    private final String track;

    public Reward(String track) {
        this.track = track != null ? track : "free";
    }

    public String getTrack() {
        return track;
    }

    public boolean isPremium() {
        return "premium".equalsIgnoreCase(track);
    }

    public abstract void give(Player player);
}
