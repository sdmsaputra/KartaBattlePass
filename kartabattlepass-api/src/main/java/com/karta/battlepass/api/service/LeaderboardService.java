package com.karta.battlepass.api.service;

import com.karta.battlepass.api.data.leaderboard.LeaderboardEntry;
import com.karta.battlepass.api.data.leaderboard.LeaderboardMetric;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A service for retrieving leaderboard data.
 */
public interface LeaderboardService {

    /**
     * Gets a paginated view of the leaderboard for a given metric.
     *
     * @param metric The metric to rank players by (e.g., POINTS, TIERS).
     * @param page The page number to retrieve (1-based).
     * @param pageSize The number of entries per page.
     * @return A {@link CompletableFuture} that completes with a list of {@link LeaderboardEntry}s for the requested page.
     */
    @NotNull
    CompletableFuture<List<LeaderboardEntry>> getLeaderboardPage(@NotNull LeaderboardMetric metric, int page, int pageSize);

    /**
     * Gets a snapshot of the top N players for a given leaderboard metric.
     *
     * @param metric The metric to rank players by.
     * @param limit The maximum number of entries to return.
     * @return A {@link CompletableFuture} that completes with a list of the top {@link LeaderboardEntry}s.
     */
    @NotNull
    CompletableFuture<List<LeaderboardEntry>> getLeaderboardSnapshot(@NotNull LeaderboardMetric metric, int limit);

    /**
     * Forces the leaderboard to be rebuilt from the data source.
     * This is an expensive operation and should be used sparingly.
     *
     * @return A {@link CompletableFuture} that completes when the rebuild is finished.
     */
    @NotNull
    CompletableFuture<Void> rebuildLeaderboard();
}
