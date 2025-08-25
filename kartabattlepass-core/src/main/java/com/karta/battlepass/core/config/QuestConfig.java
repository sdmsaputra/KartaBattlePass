package com.karta.battlepass.core.config;

import com.karta.battlepass.api.data.quest.QuestCategory;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/** Maps to a single quest file in the `quests/` directory. */
public record QuestConfig(@NotNull Map<String, QuestDefinition> quests) {
    /** Represents the definition of a single quest in the config. */
    public record QuestDefinition(
            @NotNull String name,
            @NotNull List<String> description,
            @NotNull QuestCategory category,
            @NotNull String type,
            @NotNull Map<String, Object> objectives,
            long points,
            boolean repeatable,
            @NotNull List<RewardDefinition> rewards) {}

    /** Represents a reward attached to a quest. */
    public record RewardDefinition(
            @NotNull String type, @NotNull Map<String, Object> data, @NotNull String description) {}
}
