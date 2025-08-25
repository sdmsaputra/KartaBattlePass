package com.karta.battlepass.api.service;

import com.karta.battlepass.api.data.season.Season;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

/** A service for managing Battle Pass seasons. */
public interface SeasonService {

    /**
     * Gets the currently active season, if one exists.
     *
     * @return A {@link CompletableFuture} that completes with an {@link Optional} containing the
     *     active {@link Season}, or an empty Optional if no season is currently active.
     */
    @NotNull
    CompletableFuture<Optional<Season>> getCurrentSeason();

    /**
     * Forces a reload of the season configuration from storage.
     *
     * @return A {@link CompletableFuture} that completes when the reload is finished.
     */
    @NotNull
    CompletableFuture<Void> reloadSeasons();
}
