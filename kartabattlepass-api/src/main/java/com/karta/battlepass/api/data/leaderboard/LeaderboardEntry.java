package com.karta.battlepass.api.data.leaderboard;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a single entry in a leaderboard.
 *
 * @param rank The position of the entry in the leaderboard (1-based).
 * @param playerUuid The UUID of the player.
 * @param playerName The name of the player.
 * @param value The value associated with the entry (e.g., points or tier).
 */
public record LeaderboardEntry(
        int rank, @NotNull UUID playerUuid, @NotNull String playerName, long value) {}
