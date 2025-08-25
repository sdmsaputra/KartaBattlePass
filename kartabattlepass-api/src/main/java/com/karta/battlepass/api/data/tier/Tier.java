package com.karta.battlepass.api.data.tier;

import com.karta.battlepass.api.data.pass.PassType;
import com.karta.battlepass.api.data.reward.Reward;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a single tier or level within the Battle Pass.
 *
 * @param level The level of the tier (e.g., 1, 2, 50).
 * @param pointsRequired The number of points required to unlock this tier.
 * @param freeRewards A list of rewards for the {@link PassType#FREE} track.
 * @param premiumRewards A list of rewards for the {@link PassType#PREMIUM} track.
 */
public record Tier(
    int level,
    long pointsRequired,
    @NotNull List<Reward> freeRewards,
    @NotNull List<Reward> premiumRewards
) {
}
