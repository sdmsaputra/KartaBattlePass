package com.karta.battlepass.api.service;

import com.karta.battlepass.api.data.pass.PassType;
import com.karta.battlepass.api.data.tier.Tier;
import com.karta.battlepass.api.reward.RewardType;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

/** A service for managing reward definitions and claims. */
public interface RewardService {

    /**
     * Registers a new custom reward type.
     *
     * @param identifier The unique identifier for the reward type.
     * @param rewardType The implementation of the {@link RewardType}.
     */
    void registerRewardType(@NotNull String identifier, @NotNull RewardType rewardType);

    /**
     * Gets the full list of tiers and their configured rewards for the current season.
     *
     * @return A {@link CompletableFuture} that completes with a list of all {@link Tier}
     *     definitions.
     */
    @NotNull
    CompletableFuture<List<Tier>> getTiers();

    /**
     * Checks if a player has claimed the rewards for a specific tier and pass type.
     *
     * @param playerUuid The UUID of the player.
     * @param tierLevel The level of the tier to check.
     * @param passType The pass track (FREE or PREMIUM) to check.
     * @return A {@link CompletableFuture} that completes with {@code true} if the rewards have been
     *     claimed, {@code false} otherwise.
     */
    @NotNull
    CompletableFuture<Boolean> hasClaimedTier(
            @NotNull UUID playerUuid, int tierLevel, @NotNull PassType passType);

    /**
     * Claims all available rewards for a player up to their current tier.
     *
     * @param playerUuid The UUID of the player.
     * @return A {@link CompletableFuture} that completes when all available rewards have been
     *     granted.
     */
    @NotNull
    CompletableFuture<Void> claimAllAvailableRewards(@NotNull UUID playerUuid);

    /**
     * Claims the rewards for a single, specific tier for a player.
     *
     * <p>This will fail if the player has not yet unlocked the tier or has already claimed it.
     *
     * @param playerUuid The UUID of the player.
     * @param tierLevel The level of the tier to claim.
     * @return A {@link CompletableFuture} that completes when the tier rewards have been granted.
     */
    @NotNull
    CompletableFuture<Void> claimTierReward(@NotNull UUID playerUuid, int tierLevel);

    /**
     * Forces a reload of all tier and reward definitions from the configuration files.
     *
     * @return A {@link CompletableFuture} that completes when the reload is finished.
     */
    @NotNull
    CompletableFuture<Void> reloadRewards();
}
