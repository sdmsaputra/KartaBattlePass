package com.karta.battlepass.core.db.records;

import java.time.Instant;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ActiveBoosterRecord(
        long id,
        @NotNull String boosterDefId,
        @Nullable UUID playerUuid,
        @NotNull Instant activatedAt,
        @NotNull Instant expiresAt) {}
