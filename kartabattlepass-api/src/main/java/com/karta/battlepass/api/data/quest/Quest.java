package com.karta.battlepass.api.data.quest;

import com.karta.battlepass.api.data.reward.Reward;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the static definition of a quest.
 *
 * @param id The unique identifier for this quest (e.g., "daily_break_logs").
 * @param name The display name of the quest.
 * @param description A list of strings forming the description of the quest.
 * @param category The category of the quest (DAILY, WEEKLY, SEASONAL).
 * @param type The identifier for the quest's objective logic (e.g., "BREAK_BLOCK", "KILL_MOB").
 * @param objectives A map containing the configuration for the quest's objectives. The structure of
 *     this map depends on the quest {@code type}.
 * @param points The number of battle pass points awarded upon completion.
 * @param rewards A list of direct rewards for completing this quest.
 * @param repeatable Whether this quest can be completed multiple times.
 */
public record Quest(
        @NotNull String id,
        @NotNull String name,
        @NotNull List<String> description,
        @NotNull QuestCategory category,
        @NotNull String type,
        @NotNull Map<String, Object> objectives,
        long points,
        @NotNull List<Reward> rewards,
        boolean repeatable) {}
