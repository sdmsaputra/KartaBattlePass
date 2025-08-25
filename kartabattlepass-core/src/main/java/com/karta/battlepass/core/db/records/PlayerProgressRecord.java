package com.karta.battlepass.core.db.records;

import java.time.Instant;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public record PlayerProgressRecord(
        @NotNull UUID playerUuid, long points, int tier, @NotNull Instant updatedAt) {}
