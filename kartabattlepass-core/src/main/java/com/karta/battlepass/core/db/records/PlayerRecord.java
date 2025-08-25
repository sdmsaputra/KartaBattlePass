package com.karta.battlepass.core.db.records;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

public record PlayerRecord(
    @NotNull UUID uuid,
    @NotNull String name,
    @NotNull Instant firstSeen,
    @NotNull Instant lastSeen
) {
}
