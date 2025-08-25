package com.karta.battlepass.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/** Maps to the rewards.yml file. */
public record RewardConfig(@NotNull Map<String, TierDefinition> tiers) {
    public record TierDefinition(
            @JsonProperty("points-required") long pointsRequired,
            @NotNull List<QuestConfig.RewardDefinition> free,
            @NotNull List<QuestConfig.RewardDefinition> premium) {}
}
