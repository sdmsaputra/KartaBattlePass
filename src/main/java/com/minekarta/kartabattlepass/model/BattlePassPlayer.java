package com.minekarta.kartabattlepass.model;

import java.util.List;
import java.util.UUID;

public class BattlePassPlayer {

    private final UUID uuid;
    private String name;
    private int level;
    private int exp;
    private List<String> claimedRewards;
    private List<String> completedMissions;

    public BattlePassPlayer(UUID uuid, String name, int level, int exp, List<String> claimedRewards, List<String> completedMissions) {
        this.uuid = uuid;
        this.name = name;
        this.level = level;
        this.exp = exp;
        this.claimedRewards = claimedRewards;
        this.completedMissions = completedMissions;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public List<String> getClaimedRewards() {
        return claimedRewards;
    }

    public void setClaimedRewards(List<String> claimedRewards) {
        this.claimedRewards = claimedRewards;
    }

    public List<String> getCompletedMissions() {
        return completedMissions;
    }

    public void setCompletedMissions(List<String> completedMissions) {
        this.completedMissions = completedMissions;
    }
}
