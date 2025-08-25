package com.karta.battlepass.api.service;

import com.karta.battlepass.api.data.booster.ActiveBooster;
import com.karta.battlepass.api.data.booster.Booster;
import com.karta.battlepass.api.data.booster.BoosterType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A service for managing boosters.
 */
public interface BoosterService {

    /**
     * Gets a booster definition by its ID.
     *
     * @param boosterId The ID of the booster to find.
     * @return An {@link Optional} containing the {@link Booster} if found, otherwise empty.
     */
    Optional<Booster> getBoosterById(@NotNull String boosterId);

    /**
     * Activates a booster either globally or for a specific player.
     *
     * @param boosterId The ID of the booster to activate.
     * @param playerUuid The UUID of the player to activate the booster for. If null, the booster is activated globally.
     * @return A {@link CompletableFuture} that completes with the {@link ActiveBooster} instance if successful.
     */
    @NotNull
    CompletableFuture<ActiveBooster> activateBooster(@NotNull String boosterId, @Nullable UUID playerUuid);

    /**
     * Deactivates a booster instance.
     *
     * @param activeBooster The booster instance to deactivate.
     * @return A {@link CompletableFuture} that completes when the booster is deactivated.
     */
    @NotNull
    CompletableFuture<Void> deactivateBooster(@NotNull ActiveBooster activeBooster);

    /**
     * Gets all active boosters for a specific player, including global boosters.
     *
     * @param playerUuid The UUID of the player.
     * @return A {@link CompletableFuture} that completes with a list of all relevant {@link ActiveBooster}s.
     */
    @NotNull
    CompletableFuture<List<ActiveBooster>> getActiveBoosters(@NotNull UUID playerUuid);

    /**
     * Calculates the total multiplier for a given booster type for a specific player.
     * This takes into account all active personal and global boosters and their stacking strategies.
     *
     * @param playerUuid The UUID of the player.
     * @param type The type of booster to calculate the multiplier for.
     * @return A {@link CompletableFuture} that completes with the final multiplier. A value of 1.0 means no boost.
     */
    @NotNull
    CompletableFuture<Double> getMultiplier(@NotNull UUID playerUuid, @NotNull BoosterType type);
}
