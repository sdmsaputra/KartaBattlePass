package com.karta.battlepass.core.service.impl;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.karta.battlepass.api.data.player.PlayerProfile;
import com.karta.battlepass.api.service.PlayerService;
import com.karta.battlepass.core.db.dao.PlayerDao;
import com.karta.battlepass.core.db.dao.PlayerProgressDao;
import com.karta.battlepass.core.db.records.PlayerProgressRecord;
import com.karta.battlepass.core.db.records.PlayerRecord;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import com.karta.battlepass.api.data.pass.Pass;
import com.karta.battlepass.api.data.pass.PassType;
import com.karta.battlepass.core.db.dao.PlayerDao;
import com.karta.battlepass.core.db.dao.PlayerProgressDao;
import com.karta.battlepass.core.db.records.PlayerProgressRecord;
import com.karta.battlepass.core.db.records.PlayerRecord;
import com.karta.battlepass.core.event.bus.EventBus;
import com.karta.battlepass.core.event.data.TierChangeEventData;
import com.karta.battlepass.core.scheduler.KartaScheduler;
import com.karta.battlepass.core.service.ServiceRegistry;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerServiceImpl implements PlayerService {

    private final Jdbi jdbi;
    private final KartaScheduler scheduler;
    private final ServiceRegistry registry;
    private final EventBus eventBus;
    private final AsyncLoadingCache<UUID, PlayerProfile> profileCache;

    public PlayerServiceImpl(@NotNull ServiceRegistry registry, @NotNull Jdbi jdbi, @NotNull KartaScheduler scheduler, @NotNull EventBus eventBus) {
        this.registry = registry;
        this.jdbi = jdbi;
        this.scheduler = scheduler;
        this.eventBus = eventBus;
        this.profileCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(Duration.ofMinutes(15))
                .buildAsync((uuid, executor) -> loadPlayerProfile(uuid, "Unknown").join()); // Name is unknown here
    }

    private CompletableFuture<PlayerProfile> loadPlayerProfile(UUID uuid, String name) {
        return scheduler.supplyAsync(() -> jdbi.inTransaction(handle -> {
            PlayerDao playerDao = handle.attach(PlayerDao.class);
            PlayerProgressDao progressDao = handle.attach(PlayerProgressDao.class);

            Instant now = Instant.now();
            Optional<PlayerRecord> playerRecordOpt = playerDao.findById(uuid);
            if (playerRecordOpt.isEmpty()) {
                playerDao.insert(uuid, name, now);
            } else if (!playerRecordOpt.get().name().equals(name)) {
                playerDao.update(uuid, name, now);
            }

            PlayerProgressRecord progressRecord = progressDao.findById(uuid)
                    .orElse(new PlayerProgressRecord(uuid, 0, 0, now));
            if (progressDao.findById(uuid).isEmpty()) {
                progressDao.insert(uuid, 0, 0, now);
            }

            PassType passType = registry.passService().getPass(uuid)
                    .join() // This is safe as we are inside a supplyAsync chain
                    .map(Pass::type)
                    .orElse(PassType.FREE);

            return new PlayerProfile(uuid, name, progressRecord.points(), progressRecord.tier(), passType);
        }));
    }

    /**
     * Handles the logic for when a player joins the server.
     * Ensures the player is registered in the database.
     *
     * @param uuid The player's UUID.
     * @param name The player's name.
     * @return A future that completes when the operation is done.
     */
    public CompletableFuture<Void> handlePlayerJoin(UUID uuid, String name) {
        // Preload the cache
        return profileCache.get(uuid, (u, exe) -> loadPlayerProfile(u, name)).thenAccept(profile -> {});
    }

    @Override
    public @NotNull CompletableFuture<Optional<PlayerProfile>> getPlayerProfile(@NotNull UUID playerUuid) {
        // This will fail if the player has never joined before, as name is unknown.
        // The Bukkit layer must call handlePlayerJoin first.
        return profileCache.get(playerUuid).thenApply(Optional::of);
    }

    @Override
    public @NotNull CompletableFuture<Long> getPoints(@NotNull UUID playerUuid) {
        return getPlayerProfile(playerUuid)
                .thenApply(profileOpt -> profileOpt.map(PlayerProfile::points).orElse(0L));
    }

    @Override
    public @NotNull CompletableFuture<Void> setPoints(@NotNull UUID playerUuid, long amount) {
        return scheduler.runAsync(() -> {
            jdbi.useHandle(handle -> {
                PlayerProgressDao dao = handle.attach(PlayerProgressDao.class);
                // This assumes the player progress record already exists.
                // A full implementation would handle creating it if not.
                dao.update(playerUuid, amount, 0, Instant.now()); // Tier calculation needed
            });
            profileCache.invalidate(playerUuid);
        });
    }

    @Override
    public @NotNull CompletableFuture<Long> addPoints(@NotNull UUID playerUuid, long amount) {
        return getPlayerProfile(playerUuid).thenCompose(profileOpt -> {
            if (profileOpt.isEmpty()) {
                return CompletableFuture.completedFuture(0L);
            }
            PlayerProfile currentProfile = profileOpt.get();
            long newPoints = currentProfile.points() + amount;

            return registry.rewardService().getTiers().thenCompose(tiers -> {
                int newTier = currentProfile.tier();
                for (int i = tiers.size() - 1; i >= 0; i--) {
                    if (newPoints >= tiers.get(i).pointsRequired()) {
                        newTier = tiers.get(i).level();
                        break;
                    }
                }

                final int finalNewTier = newTier;
                return scheduler.supplyAsync(() -> {
                    jdbi.useHandle(handle -> {
                        PlayerProgressDao dao = handle.attach(PlayerProgressDao.class);
                        dao.update(playerUuid, newPoints, finalNewTier, Instant.now());
                    });
                    profileCache.invalidate(playerUuid);

                    if (finalNewTier > currentProfile.tier()) {
                        eventBus.fire(new TierChangeEventData(playerUuid, currentProfile.tier(), finalNewTier));
                    }

                    return newPoints;
                });
            });
        });
    }

    @Override
    public @NotNull CompletableFuture<Integer> getTier(@NotNull UUID playerUuid) {
        return getPlayerProfile(playerUuid)
                .thenApply(profileOpt -> profileOpt.map(PlayerProfile::tier).orElse(0));
    }

    @Override
    public @NotNull CompletableFuture<Void> setTier(@NotNull UUID playerUuid, int tier) {
        return scheduler.runAsync(() -> {
            jdbi.useHandle(handle -> {
                PlayerProgressDao dao = handle.attach(PlayerProgressDao.class);
                // This assumes the player progress record already exists.
                // A full implementation would handle creating it if not.
                // TODO: Also needs to calculate points for the tier.
                dao.update(playerUuid, 0, tier, Instant.now());
            });
            profileCache.invalidate(playerUuid);
        });
    }

    @Override
    public void invalidateCache(@NotNull UUID playerUuid) {
        profileCache.invalidate(playerUuid);
    }
}
