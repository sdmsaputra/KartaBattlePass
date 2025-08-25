package com.karta.battlepass.core.service;

import com.karta.battlepass.api.service.BoosterService;
import com.karta.battlepass.api.service.LeaderboardService;
import com.karta.battlepass.api.service.PassService;
import com.karta.battlepass.api.service.PlayerService;
import com.karta.battlepass.api.service.QuestService;
import com.karta.battlepass.api.service.RewardService;
import com.karta.battlepass.api.service.SeasonService;
import java.util.HashMap;
import java.util.Map;

/** A central registry for all services to resolve dependencies lazily. */
@SuppressWarnings("unchecked")
public class ServiceRegistry {

    private final Map<Class<?>, Object> services = new HashMap<>();

    public <T> void register(Class<T> serviceClass, T serviceInstance) {
        services.put(serviceClass, serviceInstance);
    }

    public <T> T get(Class<T> serviceClass) {
        T service = (T) services.get(serviceClass);
        if (service == null) {
            throw new IllegalStateException(
                    "Service not registered: " + serviceClass.getSimpleName());
        }
        return service;
    }

    // Convenience getters
    public PlayerService playerService() {
        return get(PlayerService.class);
    }

    public SeasonService seasonService() {
        return get(SeasonService.class);
    }

    public PassService passService() {
        return get(PassService.class);
    }

    public QuestService questService() {
        return get(QuestService.class);
    }

    public RewardService rewardService() {
        return get(RewardService.class);
    }

    public BoosterService boosterService() {
        return get(BoosterService.class);
    }

    public LeaderboardService leaderboardService() {
        return get(LeaderboardService.class);
    }
}
