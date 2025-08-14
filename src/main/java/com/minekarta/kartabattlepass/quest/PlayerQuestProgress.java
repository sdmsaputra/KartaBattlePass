package com.minekarta.kartabattlepass.quest;

public class PlayerQuestProgress {

    private final String questId;
    private int currentAmount;
    private boolean completed;

    public PlayerQuestProgress(String questId) {
        this.questId = questId;
        this.currentAmount = 0;
        this.completed = false;
    }

    public String getQuestId() {
        return questId;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }

    public void incrementAmount(int amount) {
        this.currentAmount += amount;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
