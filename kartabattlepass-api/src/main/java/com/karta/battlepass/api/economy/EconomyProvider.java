package com.karta.battlepass.api.economy;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

/**
 * A service provider interface for handling economy operations.
 *
 * <p>This allows KartaBattlePass to integrate with various economy plugins without depending on
 * them directly. Implementations should be thread-safe.
 */
public interface EconomyProvider {

    /**
     * Gets the unique name of this economy provider (e.g., "Vault", "KARTA_EMERALD").
     *
     * @return The name of the provider.
     */
    @NotNull
    String getName();

    /**
     * Gets the balance of a player.
     *
     * @param playerUuid The UUID of the player to check the balance of.
     * @return A {@link CompletableFuture} that completes with the player's balance.
     */
    @NotNull
    CompletableFuture<BigDecimal> getBalance(@NotNull UUID playerUuid);

    /**
     * Withdraws an amount from a player's balance.
     *
     * @param playerUuid The UUID of the player to withdraw from.
     * @param amount The amount to withdraw.
     * @return A {@link CompletableFuture} that completes with {@code true} if the transaction was
     *     successful, {@code false} otherwise (e.g., insufficient funds).
     */
    @NotNull
    CompletableFuture<Boolean> withdraw(@NotNull UUID playerUuid, @NotNull BigDecimal amount);

    /**
     * Deposits an amount into a player's balance.
     *
     * @param playerUuid The UUID of the player to deposit to.
     * @param amount The amount to deposit.
     * @return A {@link CompletableFuture} that completes with {@code true} if the transaction was
     *     successful, {@code false} otherwise.
     */
    @NotNull
    CompletableFuture<Boolean> deposit(@NotNull UUID playerUuid, @NotNull BigDecimal amount);

    /**
     * Checks if a player has at least a certain amount in their balance.
     *
     * @param playerUuid The UUID of the player to check.
     * @param amount The amount to check for.
     * @return A {@link CompletableFuture} that completes with {@code true} if the player has enough
     *     funds, {@code false} otherwise.
     */
    @NotNull
    default CompletableFuture<Boolean> hasFunds(
            @NotNull UUID playerUuid, @NotNull BigDecimal amount) {
        return getBalance(playerUuid).thenApply(balance -> balance.compareTo(amount) >= 0);
    }
}
