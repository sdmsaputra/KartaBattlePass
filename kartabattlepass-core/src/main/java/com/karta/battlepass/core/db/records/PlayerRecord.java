package com.karta.battlepass.core.db.records;

import java.time.Instant;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public record PlayerRecord(
        @NotNull UUID uuid,
        @NotNull String name,
        @NotNull Instant firstSeen,
        @NotNull Instant lastSeen) {}
