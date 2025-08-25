package com.karta.battlepass.api.service;

import com.karta.battlepass.api.data.player.PlayerProfile;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A service for managing player-specific data within the Battle Pass system.
 * <p>
 * All data operations are asynchronous and return a {@link CompletableFuture}.
 */
public interface PlayerService {

    /**
     * Fetches a player's profile containing their current battle pass status.
     *
     * @param playerUuid The UUID of the player to fetch.
     * @return A {@link CompletableFuture} that will complete with an {@link Optional}
     *         containing the player's profile if they exist, or an empty Optional otherwise.
     */
    @NotNull
    CompletableFuture<Optional<PlayerProfile>> getPlayerProfile(@NotNull UUID playerUuid);

    /**
     * Gets the current points for a player.
     *
     * @param playerUuid The UUID of the player.
     * @return A {@link CompletableFuture} that will complete with the player's points, or 0 if not found.
     */
    @NotNull
    CompletableFuture<Long> getPoints(@NotNull UUID playerUuid);

    /**
     * Sets a player's points to a specific amount.
     *
     * @param playerUuid The UUID of the player.
     * @param amount The new total number of points.
     * @return A {@link CompletableFuture} that completes when the operation is finished.
     */
    @NotNull
    CompletableFuture<Void> setPoints(@NotNull UUID playerUuid, long amount);

    /**
     * Adds a specified number of points to a player's total.
     * This operation is atomic.
     *
     * @param playerUuid The UUID of the player.
     * @param amount The number of points to add (can be negative).
     * @return A {@link CompletableFuture} that completes with the new point total.
     */
    @NotNull
    CompletableFuture<Long> addPoints(@NotNull UUID playerUuid, long amount);

    /**
     * Gets the current tier for a player.
     *
     * @param playerUuid The UUID of the player.
     * @return A {@link CompletableFuture} that will complete with the player's tier, or 0 if not found.
     */
    @NotNull
    CompletableFuture<Integer> getTier(@NotNull UUID playerUuid);

    /**
     * Sets a player's tier to a specific level.
     * Note: This typically also adjusts the player's points to the minimum required for that tier.
     *
     * @param playerUuid The UUID of the player.
     * @param tier The new tier level.
     * @return A {@link CompletableFuture} that completes when the operation is finished.
     */
    @NotNull
    CompletableFuture<Void> setTier(@NotNull UUID playerUuid, int tier);

    /**
     * Invalidates a player's profile from the cache.
     * This should be called when an external factor changes the player's state,
     * such as their pass type being upgraded.
     *
     * @param playerUuid The UUID of the player to invalidate.
     */
    void invalidateCache(@NotNull UUID playerUuid);
}
