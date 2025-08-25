package com.karta.battlepass.api.data.quest;

/** Represents the category of a quest, which typically determines its reset frequency. */
public enum QuestCategory {
    /** A quest that resets daily. */
    DAILY,
    /** A quest that resets weekly. */
    WEEKLY,
    /** A quest that lasts for the entire season. */
    SEASONAL
}
