package com.karta.battlepass.api.data.booster;

import java.time.Instant;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an instance of a booster that is currently active.
 *
 * @param booster The static definition of the booster.
 * @param playerUuid The UUID of the player this booster is active for. If null, this is a global
 *     booster.
 * @param activatedAt The timestamp when the booster was activated.
 * @param expiresAt The timestamp when the booster will expire.
 */
public record ActiveBooster(
        @NotNull Booster booster,
        @Nullable UUID playerUuid,
        @NotNull Instant activatedAt,
        @NotNull Instant expiresAt) {}
