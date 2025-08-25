package com.karta.battlepass.core.db.records;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

public record RewardClaimedRecord(
    @NotNull UUID playerUuid,
    @NotNull String rewardId,
    @NotNull Instant claimedAt
) {
}
