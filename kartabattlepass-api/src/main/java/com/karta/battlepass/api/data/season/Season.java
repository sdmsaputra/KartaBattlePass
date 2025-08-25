package com.karta.battlepass.api.data.season;

import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;

/**
 * Represents a Battle Pass season with its defined time window.
 *
 * @param id The unique identifier for the season.
 * @param name The display name of the season (e.g., "Season 1").
 * @param start The zoned date and time when the season starts.
 * @param end The zoned date and time when the season ends.
 */
public record Season(
    long id,
    @NotNull String name,
    @NotNull ZonedDateTime start,
    @NotNull ZonedDateTime end
) {
    /**
     * Checks if the season is currently active.
     *
     * @return {@code true} if the current time is between the start and end times, {@code false} otherwise.
     */
    public boolean isActive() {
        ZonedDateTime now = ZonedDateTime.now(start.getZone());
        return !now.isBefore(start) && now.isBefore(end);
    }
}
