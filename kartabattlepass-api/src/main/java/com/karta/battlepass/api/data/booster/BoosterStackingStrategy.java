package com.karta.battlepass.api.data.booster;

/**
 * Defines how multiple active boosters of the same type should combine their effects.
 */
public enum BoosterStackingStrategy {
    /**
     * Sums the multipliers of all active boosters.
     * Example: 1.5x + 1.5x = 3.0x total multiplier.
     */
    SUM,
    /**
     * Sums the multipliers of all active boosters, but only if they are from different sources (IDs).
     * If multiple boosters with the same ID are active, only the one with the highest multiplier is used.
     */
    SUM_DIFFERENT,
    /**
     * Only the single highest multiplier from all active boosters is applied.
     */
    HIGHEST
}
