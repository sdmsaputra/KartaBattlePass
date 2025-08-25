package com.karta.battlepass.api;

import com.karta.battlepass.api.service.BoosterService;
import com.karta.battlepass.api.service.LeaderboardService;
import com.karta.battlepass.api.service.PassService;
import com.karta.battlepass.api.service.PlayerService;
import com.karta.battlepass.api.service.QuestService;
import com.karta.battlepass.api.service.RewardService;
import com.karta.battlepass.api.service.SeasonService;
import org.jetbrains.annotations.NotNull;

/**
 * The main entry point for the KartaBattlePass API.
 *
 * <p>This interface provides access to all the core services of the plugin. An instance of this API
 * can be obtained from the Bukkit ServicesManager.
 *
 * <pre>{@code
 * RegisteredServiceProvider<KartaBattlePassAPI> provider = Bukkit.getServicesManager().getRegistration(KartaBattlePassAPI.class);
 * if (provider != null) {
 *     KartaBattlePassAPI api = provider.getProvider();
 *     // Use the API
 * }
 * }</pre>
 */
public interface KartaBattlePassAPI {

    /**
     * Gets the current version of the KartaBattlePass API.
     *
     * @return The API version string.
     */
    @NotNull
    String getApiVersion();

    /**
     * Gets the service responsible for managing player data.
     *
     * @return The {@link PlayerService}.
     */
    @NotNull
    PlayerService getPlayerService();

    /**
     * Gets the service responsible for managing seasons.
     *
     * @return The {@link SeasonService}.
     */
    @NotNull
    SeasonService getSeasonService();

    /**
     * Gets the service responsible for managing battle passes.
     *
     * @return The {@link PassService}.
     */
    @NotNull
    PassService getPassService();

    /**
     * Gets the service responsible for managing quests and their progress.
     *
     * @return The {@link QuestService}.
     */
    @NotNull
    QuestService getQuestService();

    /**
     * Gets the service responsible for managing rewards.
     *
     * @return The {@link RewardService}.
     */
    @NotNull
    RewardService getRewardService();

    /**
     * Gets the service responsible for managing boosters.
     *
     * @return The {@link BoosterService}.
     */
    @NotNull
    BoosterService getBoosterService();

    /**
     * Gets the service responsible for managing leaderboards.
     *
     * @return The {@link LeaderboardService}.
     */
    @NotNull
    LeaderboardService getLeaderboardService();
}
