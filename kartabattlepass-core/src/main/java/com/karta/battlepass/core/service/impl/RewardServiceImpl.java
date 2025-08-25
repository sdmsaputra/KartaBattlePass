package com.karta.battlepass.core.service.impl;

import com.karta.battlepass.api.data.pass.PassType;
import com.karta.battlepass.api.data.reward.Reward;
import com.karta.battlepass.api.data.tier.Tier;
import com.karta.battlepass.api.reward.RewardType;
import com.karta.battlepass.api.service.RewardService;
import com.karta.battlepass.core.config.ConfigManager;
import com.karta.battlepass.core.config.QuestConfig;
import com.karta.battlepass.core.config.RewardConfig;
import com.karta.battlepass.core.db.dao.RewardClaimedDao;
import com.karta.battlepass.core.scheduler.KartaScheduler;
import com.karta.battlepass.core.service.ServiceRegistry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;

public class RewardServiceImpl implements RewardService {

    private final ServiceRegistry registry;
    private final ConfigManager configManager;
    private final KartaScheduler scheduler;
    private final Jdbi jdbi;
    private final Map<String, RewardType> rewardTypeRegistry = new ConcurrentHashMap<>();
    private List<Tier> tierRegistry = new ArrayList<>();

    public RewardServiceImpl(
            ServiceRegistry registry,
            ConfigManager configManager,
            KartaScheduler scheduler,
            Jdbi jdbi) {
        this.registry = registry;
        this.configManager = configManager;
        this.scheduler = scheduler;
        this.jdbi = jdbi;
        reloadRewards().join(); // Initial load
    }

    @Override
    public void registerRewardType(@NotNull String identifier, @NotNull RewardType rewardType) {
        rewardTypeRegistry.put(identifier.toLowerCase(), rewardType);
    }

    @Override
    public @NotNull CompletableFuture<List<Tier>> getTiers() {
        return CompletableFuture.completedFuture(Collections.unmodifiableList(tierRegistry));
    }

    @Override
    public @NotNull CompletableFuture<Boolean> hasClaimedTier(
            @NotNull UUID playerUuid, int tierLevel, @NotNull PassType passType) {
        String rewardId = "tier_" + tierLevel + "_" + passType.name().toLowerCase();
        return scheduler.supplyAsync(
                () ->
                        jdbi.withHandle(
                                handle ->
                                        handle.attach(RewardClaimedDao.class)
                                                .findById(playerUuid, rewardId)
                                                .isPresent()));
    }

    @Override
    public @NotNull CompletableFuture<Void> claimAllAvailableRewards(@NotNull UUID playerUuid) {
        // TODO: Implement logic
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public @NotNull CompletableFuture<Void> claimTierReward(
            @NotNull UUID playerUuid, int tierLevel) {
        // TODO: Implement logic
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public @NotNull CompletableFuture<Void> reloadRewards() {
        return scheduler.supplyAsync(
                () -> {
                    try {
                        RewardConfig config =
                                configManager.loadConfig("rewards.yml", RewardConfig.class);
                        List<Tier> loadedTiers = new ArrayList<>();
                        config.tiers()
                                .forEach(
                                        (levelStr, def) -> {
                                            int level = Integer.parseInt(levelStr);
                                            Tier tier = toApi(level, def);
                                            loadedTiers.add(tier);
                                        });
                        loadedTiers.sort(java.util.Comparator.comparingInt(Tier::level));
                        this.tierRegistry = loadedTiers;
                    } catch (IOException e) {
                        e.printStackTrace(); // Log this properly
                    }
                    return null;
                });
    }

    private Tier toApi(int level, RewardConfig.TierDefinition def) {
        List<Reward> freeRewards =
                def.free().stream()
                        .map(r -> toApiReward("tier_" + level + "_free", r))
                        .collect(Collectors.toList());
        List<Reward> premiumRewards =
                def.premium().stream()
                        .map(r -> toApiReward("tier_" + level + "_premium", r))
                        .collect(Collectors.toList());
        return new Tier(level, def.pointsRequired(), freeRewards, premiumRewards);
    }

    private Reward toApiReward(String tierId, QuestConfig.RewardDefinition r) {
        return new Reward(tierId + "_" + r.type(), r.type(), r.data(), r.description());
    }
}
