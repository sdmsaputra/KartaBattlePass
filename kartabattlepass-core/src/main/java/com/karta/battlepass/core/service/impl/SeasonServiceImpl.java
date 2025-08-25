package com.karta.battlepass.core.service.impl;

import com.karta.battlepass.api.data.season.Season;
import com.karta.battlepass.api.service.SeasonService;
import com.karta.battlepass.core.config.MainConfig;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.NotNull;

public class SeasonServiceImpl implements SeasonService {

    private final AtomicReference<MainConfig> config;

    public SeasonServiceImpl(AtomicReference<MainConfig> config) {
        this.config = config;
    }

    @Override
    public @NotNull CompletableFuture<Optional<Season>> getCurrentSeason() {
        return CompletableFuture.supplyAsync(
                () -> {
                    MainConfig.SeasonConfig seasonConfig = config.get().season();
                    Season season =
                            new Season(
                                    1L, // ID will come from DB in a multi-season setup
                                    seasonConfig.name(),
                                    seasonConfig.start(),
                                    seasonConfig.end());
                    return season.isActive() ? Optional.of(season) : Optional.empty();
                });
    }

    @Override
    public @NotNull CompletableFuture<Void> reloadSeasons() {
        // This would be handled by the ConfigManager reloading the main config.
        // The AtomicReference ensures all services see the new config.
        return CompletableFuture.completedFuture(null);
    }
}
