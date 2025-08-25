package com.karta.battlepass.core.db.records;

import com.karta.battlepass.api.data.pass.PassType;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

public record PassRecord(
    long id,
    long seasonId,
    @NotNull UUID playerUuid,
    @NotNull PassType passType,
    @NotNull Instant purchaseTime
) {
}
