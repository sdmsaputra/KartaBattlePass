package com.karta.battlepass.api.data.quest;

/**
 * Represents the state of a player's progress on a quest.
 */
public enum QuestStatus {
    /**
     * The player has not yet completed the quest objectives.
     */
    IN_PROGRESS,
    /**
     * The player has completed the quest objectives but has not yet claimed the rewards.
     */
    COMPLETED,
    /**
     * The player has completed the quest and claimed the rewards.
     */
    CLAIMED
}
