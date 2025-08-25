package com.karta.battlepass.core.db.records;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

public record PlayerProgressRecord(
    @NotNull UUID playerUuid,
    long points,
    int tier,
    @NotNull Instant updatedAt
) {
}
