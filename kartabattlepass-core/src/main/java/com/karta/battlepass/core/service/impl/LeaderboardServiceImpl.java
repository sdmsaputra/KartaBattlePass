package com.karta.battlepass.core.service.impl;

import com.karta.battlepass.api.data.leaderboard.LeaderboardEntry;
import com.karta.battlepass.api.data.leaderboard.LeaderboardMetric;
import com.karta.battlepass.api.service.LeaderboardService;
import com.karta.battlepass.core.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LeaderboardServiceImpl implements LeaderboardService {

    private final ServiceRegistry registry;

    public LeaderboardServiceImpl(ServiceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public @NotNull CompletableFuture<List<LeaderboardEntry>> getLeaderboardPage(@NotNull LeaderboardMetric metric, int page, int pageSize) {
        // TODO: Implement logic
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public @NotNull CompletableFuture<List<LeaderboardEntry>> getLeaderboardSnapshot(@NotNull LeaderboardMetric metric, int limit) {
        // TODO: Implement logic
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public @NotNull CompletableFuture<Void> rebuildLeaderboard() {
        // TODO: Implement logic
        return CompletableFuture.completedFuture(null);
    }
}
