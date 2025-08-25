package com.karta.battlepass.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Maps to the rewards.yml file.
 */
public record RewardConfig(
    @NotNull Map<String, TierDefinition> tiers
) {
    public record TierDefinition(
        @JsonProperty("points-required") long pointsRequired,
        @NotNull List<QuestConfig.RewardDefinition> free,
        @NotNull List<QuestConfig.RewardDefinition> premium
    ) {}
}
