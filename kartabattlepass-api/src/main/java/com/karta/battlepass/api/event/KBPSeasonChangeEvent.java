package com.karta.battlepass.api.event;

import com.karta.battlepass.api.data.season.Season;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a season starts or ends.
 */
public class KBPSeasonChangeEvent extends KartaBattlePassEvent {
    private final Season oldSeason;
    private final Season newSeason;

    public KBPSeasonChangeEvent(@Nullable Season oldSeason, @Nullable Season newSeason) {
        this.oldSeason = oldSeason;
        this.newSeason = newSeason;
    }

    /**
     * Gets the season that just ended.
     * @return The previous season, or null if there was no previous season.
     */
    @Nullable
    public Season getOldSeason() {
        return oldSeason;
    }

    /**
     * Gets the season that just started.
     * @return The new season, or null if no new season is starting.
     */
    @Nullable
    public Season getNewSeason() {
        return newSeason;
    }
}
