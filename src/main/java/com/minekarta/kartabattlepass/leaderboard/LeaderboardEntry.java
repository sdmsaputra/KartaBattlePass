package com.minekarta.kartabattlepass.leaderboard;

public class LeaderboardEntry {

    private final String playerName;
    private final int level;
    private final int exp;
    private final int rank;

    public LeaderboardEntry(String playerName, int level, int exp, int rank) {
        this.playerName = playerName;
        this.level = level;
        this.exp = exp;
        this.rank = rank;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getRank() {
        return rank;
    }
}
