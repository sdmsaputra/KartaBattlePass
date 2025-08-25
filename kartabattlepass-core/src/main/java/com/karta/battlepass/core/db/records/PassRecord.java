package com.karta.battlepass.core.db.records;

import com.karta.battlepass.api.data.pass.PassType;
import java.time.Instant;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public record PassRecord(
        long id,
        long seasonId,
        @NotNull UUID playerUuid,
        @NotNull PassType passType,
        @NotNull Instant purchaseTime) {}
