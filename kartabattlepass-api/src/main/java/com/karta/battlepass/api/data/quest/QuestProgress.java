package com.karta.battlepass.api.data.quest;

import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a player's progress towards completing a quest.
 *
 * @param playerUuid The UUID of the player.
 * @param questId The ID of the quest this progress is for.
 * @param progress A map holding the player's current progress data. For a simple "break N blocks"
 *     quest, this might be `{"value": 10}`. For multi-step quests, it could be more complex.
 * @param status The current completion status of the quest for the player.
 */
public record QuestProgress(
        @NotNull UUID playerUuid,
        @NotNull String questId,
        @NotNull Map<String, Object> progress,
        @NotNull QuestStatus status) {}
