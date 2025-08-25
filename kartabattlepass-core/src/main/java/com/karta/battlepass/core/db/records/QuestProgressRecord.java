package com.karta.battlepass.core.db.records;

import com.karta.battlepass.api.data.quest.QuestStatus;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record QuestProgressRecord(
    @NotNull UUID playerUuid,
    @NotNull String questId,
    @NotNull Map<String, Object> progressData,
    @NotNull QuestStatus status,
    @NotNull Instant updatedAt
) {
}
