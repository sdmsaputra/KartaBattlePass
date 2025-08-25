package com.karta.battlepass.core.db.records;

import java.time.Instant;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public record RewardClaimedRecord(
        @NotNull UUID playerUuid, @NotNull String rewardId, @NotNull Instant claimedAt) {}
