package com.karta.battlepass.api.data.booster;

import java.time.Duration;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the static definition of a booster.
 *
 * @param id The unique identifier for this booster definition (e.g., "global_2x_points").
 * @param name The display name of the booster.
 * @param type The type of benefit this booster provides.
 * @param multiplier The factor by which to multiply the value (e.g., 2.0 for a 2x booster).
 * @param duration The duration for which the booster is active.
 * @param scope The scope of the booster's effect.
 * @param stackingStrategy The strategy to use when this booster is active with others of the same
 *     type.
 */
public record Booster(
        @NotNull String id,
        @NotNull String name,
        @NotNull BoosterType type,
        double multiplier,
        @NotNull Duration duration,
        @NotNull BoosterScope scope,
        @NotNull BoosterStackingStrategy stackingStrategy) {}
