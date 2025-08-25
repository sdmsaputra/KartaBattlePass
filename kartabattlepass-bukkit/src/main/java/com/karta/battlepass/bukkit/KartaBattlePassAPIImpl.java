package com.karta.battlepass.bukkit;

import com.karta.battlepass.api.KartaBattlePassAPI;
import com.karta.battlepass.api.service.BoosterService;
import com.karta.battlepass.api.service.LeaderboardService;
import com.karta.battlepass.api.service.PassService;
import com.karta.battlepass.api.service.PlayerService;
import com.karta.battlepass.api.service.QuestService;
import com.karta.battlepass.api.service.RewardService;
import com.karta.battlepass.api.service.SeasonService;
import com.karta.battlepass.core.service.ServiceRegistry;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class KartaBattlePassAPIImpl implements KartaBattlePassAPI {

    private final ServiceRegistry registry;
    private final Plugin plugin;

    public KartaBattlePassAPIImpl(ServiceRegistry registry, Plugin plugin) {
        this.registry = registry;
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getApiVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @NotNull PlayerService getPlayerService() {
        return registry.playerService();
    }

    @Override
    public @NotNull SeasonService getSeasonService() {
        return registry.seasonService();
    }

    @Override
    public @NotNull PassService getPassService() {
        return registry.passService();
    }

    @Override
    public @NotNull QuestService getQuestService() {
        return registry.questService();
    }

    @Override
    public @NotNull RewardService getRewardService() {
        return registry.rewardService();
    }

    @Override
    public @NotNull BoosterService getBoosterService() {
        return registry.boosterService();
    }

    @Override
    public @NotNull LeaderboardService getLeaderboardService() {
        return registry.leaderboardService();
    }
}
