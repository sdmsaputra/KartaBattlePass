package com.karta.battlepass.api.service;

import com.karta.battlepass.api.data.pass.Pass;
import com.karta.battlepass.api.data.pass.PassType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A service for managing player ownership of Battle Passes.
 */
public interface PassService {

    /**
     * Gets a player's pass for the current season.
     *
     * @param playerUuid The UUID of the player.
     * @return A {@link CompletableFuture} that completes with an {@link Optional} containing the player's
     *         {@link Pass} if they have one, or an empty Optional otherwise.
     */
    @NotNull
    CompletableFuture<Optional<Pass>> getPass(@NotNull UUID playerUuid);

    /**
     * Checks if a player has a premium pass for the current season.
     *
     * @param playerUuid The UUID of the player.
     * @return A {@link CompletableFuture} that completes with {@code true} if the player has a premium pass,
     *         {@code false} otherwise.
     */
    @NotNull
    CompletableFuture<Boolean> hasPremiumPass(@NotNull UUID playerUuid);

    /**
     * Gives a player a pass of a specific type for the current season.
     * If the player already has a pass of the same or higher type, this operation may do nothing.
     * For example, giving a FREE pass to a player who already has a PREMIUM pass will not downgrade them.
     *
     * @param playerUuid The UUID of the player to give the pass to.
     * @param type The type of pass to give.
     * @return A {@link CompletableFuture} that completes with the resulting {@link Pass} for the player.
     */
    @NotNull
    CompletableFuture<Pass> givePass(@NotNull UUID playerUuid, @NotNull PassType type);
}
