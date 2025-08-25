package com.karta.battlepass.api.data.pass;

import java.time.Instant;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a player's ownership of a specific battle pass for a season.
 *
 * @param playerUuid The UUID of the player who owns the pass.
 * @param seasonId The unique identifier of the season this pass belongs to.
 * @param type The type of the pass (FREE or PREMIUM).
 * @param acquiredAt The timestamp when the pass was acquired.
 */
public record Pass(
        @NotNull UUID playerUuid,
        long seasonId,
        @NotNull PassType type,
        @NotNull Instant acquiredAt) {}
