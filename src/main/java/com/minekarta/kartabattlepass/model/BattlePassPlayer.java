package com.minekarta.kartabattlepass.model;

import com.minekarta.kartabattlepass.KartaBattlePass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class BattlePassPlayer {

    private final UUID uuid;
    private String name;
    private int level;
    private int exp;
    private List<Integer> claimedRewards;

    public BattlePassPlayer(UUID uuid, String name, int level, int exp, List<Integer> claimedRewards) {
        this.uuid = uuid;
        this.name = name;
        this.level = level;
        this.exp = exp;
        this.claimedRewards = claimedRewards;
    }

    /**
     * Adds experience to the player and handles leveling up.
     *
     * @param amount The amount of experience to add.
     */
    public void addExp(int amount) {
        int maxLevel = KartaBattlePass.getInstance().getConfig().getInt("maxLevel", 50);
        if (this.level >= maxLevel) {
            return; // Already at max level, do nothing.
        }

        this.exp += amount;

        int expPerLevel = KartaBattlePass.getInstance().getConfig().getInt("expPerLevel", 1000);
        if (expPerLevel <= 0) {
            KartaBattlePass.getInstance().getLogger().warning("expPerLevel is not configured correctly. Please set it to a positive integer.");
            return;
        }

        while (this.exp >= expPerLevel) {
            if (this.level >= maxLevel) {
                this.exp = 0;
                break;
            }

            this.level++;
            this.exp -= expPerLevel;

            // Optional: Announce level up
            Player player = Bukkit.getPlayer(this.uuid);
            if (player != null) {
                player.sendMessage("§aCongratulations! You have reached Battle Pass Level " + this.level);
            }
        }
    }

    public boolean hasClaimed(int level) {
        return this.claimedRewards.contains(level);
    }

    public void claimReward(int level) {
        if (!hasClaimed(level)) {
            this.claimedRewards.add(level);
        }
    }

    // --- Getters and Setters ---

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

    public List<Integer> getClaimedRewards() {
        return claimedRewards;
    }

    public void setClaimedRewards(List<Integer> claimedRewards) {
        this.claimedRewards = claimedRewards;
    }
}
