package com.minekarta.kartabattlepass.model;

import com.minekarta.kartabattlepass.quest.PlayerQuestProgress;

import java.util.*;

public class BattlePassPlayer {

    private final UUID uuid;
    private String name;
    private int level;
    private int exp;
    private final Map<Integer, List<String>> claimedRewards;
    private final Map<String, PlayerQuestProgress> questProgress;
    private final Map<String, Integer> questCategoryProgress;

    public BattlePassPlayer(UUID uuid, String name, int level, int exp, Map<Integer, List<String>> claimedRewards, Map<String, PlayerQuestProgress> questProgress, Map<String, Integer> questCategoryProgress) {
        this.uuid = uuid;
        this.name = name;
        this.level = level;
        this.exp = exp;
        this.claimedRewards = claimedRewards != null ? claimedRewards : new HashMap<>();
        this.questProgress = questProgress != null ? questProgress : new HashMap<>();
        this.questCategoryProgress = questCategoryProgress != null ? questCategoryProgress : new HashMap<>();
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

    public Map<Integer, List<String>> getClaimedRewards() {
        return claimedRewards;
    }

    public boolean hasClaimedReward(int level, String rewardId) {
        return claimedRewards.getOrDefault(level, Collections.emptyList()).contains(rewardId);
    }

    public void addClaimedReward(int level, String rewardId) {
        claimedRewards.computeIfAbsent(level, k -> new ArrayList<>()).add(rewardId);
    }

    public Map<String, PlayerQuestProgress> getQuestProgress() {
        return questProgress;
    }

    public PlayerQuestProgress getQuestProgress(String questId) {
        return questProgress.get(questId);
    }

    public void addQuestProgress(PlayerQuestProgress progress) {
        this.questProgress.put(progress.getQuestId(), progress);
    }

    public Map<String, Integer> getQuestCategoryProgress() {
        return questCategoryProgress;
    }

    public int getCategoryProgress(String categoryId) {
        return questCategoryProgress.getOrDefault(categoryId, 0);
    }

    public void advanceCategoryProgress(String categoryId) {
        questCategoryProgress.put(categoryId, getCategoryProgress(categoryId) + 1);
    }
}
