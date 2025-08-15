package com.minekarta.kartabattlepass.quest;

import java.util.List;

public class Quest {

    private final String id;
    private final String type;
    private final String target;
    private final String displayName;
    private final int amount;
    private final int exp;
    private final List<String> rewards;

    public Quest(String id, String type, String target, String displayName, int amount, int exp, List<String> rewards) {
        this.id = id;
        this.type = type;
        this.target = target;
        this.displayName = displayName;
        this.amount = amount;
        this.exp = exp;
        this.rewards = rewards;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getAmount() {
        return amount;
    }

    public int getExp() {
        return exp;
    }

    public List<String> getRewards() {
        return rewards;
    }
}
