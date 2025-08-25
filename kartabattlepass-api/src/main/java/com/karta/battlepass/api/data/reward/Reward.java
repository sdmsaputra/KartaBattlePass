package com.karta.battlepass.api.data.reward;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a reward that can be given to a player.
 *
 * @param id A unique identifier for this reward instance, used for tracking claims.
 * @param type The identifier for the reward's logic (e.g., "COMMAND", "ITEM", "CURRENCY").
 * @param data A map containing the configuration for the reward. The structure of this map depends
 *     on the reward {@code type}. For example, a "COMMAND" type might have a "command" key with the
 *     command string.
 * @param description A short, user-friendly description of the reward.
 */
public record Reward(
        @NotNull String id,
        @NotNull String type,
        @NotNull Map<String, Object> data,
        @NotNull String description) {}
