package com.karta.battlepass.core.service.impl;

import com.karta.battlepass.api.data.pass.Pass;
import com.karta.battlepass.api.data.pass.PassType;
import com.karta.battlepass.api.service.PassService;
import com.karta.battlepass.core.db.dao.PassDao;
import com.karta.battlepass.core.db.records.PassRecord;
import com.karta.battlepass.core.scheduler.KartaScheduler;
import com.karta.battlepass.core.service.ServiceRegistry;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PassServiceImpl implements PassService {

    private final ServiceRegistry registry;
    private final KartaScheduler scheduler;
    private final Jdbi jdbi;

    public PassServiceImpl(ServiceRegistry registry, KartaScheduler scheduler, Jdbi jdbi) {
        this.registry = registry;
        this.scheduler = scheduler;
        this.jdbi = jdbi;
    }

    @Override
    public @NotNull CompletableFuture<Optional<Pass>> getPass(@NotNull UUID playerUuid) {
        return registry.seasonService().getCurrentSeason().thenCompose(seasonOpt -> {
            if (seasonOpt.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            return scheduler.supplyAsync(() ->
                    jdbi.withHandle(handle -> handle.attach(PassDao.class)
                            .findByPlayerAndSeason(playerUuid, seasonOpt.get().id())
                            .map(this::toApi))
            );
        });
    }

    @Override
    public @NotNull CompletableFuture<Boolean> hasPremiumPass(@NotNull UUID playerUuid) {
        return getPass(playerUuid)
                .thenApply(passOpt -> passOpt.map(pass -> pass.type() == PassType.PREMIUM).orElse(false));
    }

    @Override
    public @NotNull CompletableFuture<Pass> givePass(@NotNull UUID playerUuid, @NotNull PassType type) {
        return registry.seasonService().getCurrentSeason().thenCompose(seasonOpt -> {
            if (seasonOpt.isEmpty()) {
                throw new IllegalStateException("No active season to give a pass for.");
            }
            final long seasonId = seasonOpt.get().id();

            return scheduler.supplyAsync(() -> jdbi.inTransaction(handle -> {
                PassDao dao = handle.attach(PassDao.class);
                Optional<PassRecord> existing = dao.findByPlayerAndSeason(playerUuid, seasonId);

                if (existing.isPresent()) {
                    // Don't downgrade a premium pass
                    if (existing.get().passType() == PassType.PREMIUM || type == PassType.FREE) {
                        return toApi(existing.get());
                    }
                    // Upgrade to premium is not handled here, should be a separate method
                    // This method is for granting, not purchasing/upgrading
                    return toApi(existing.get());
                }

                long newId = dao.insert(seasonId, playerUuid, type.name(), Instant.now());
                // Invalidate player profile cache so it picks up the new pass type on next load
                registry.playerService().invalidateCache(playerUuid);
                return new Pass(playerUuid, seasonId, type, Instant.now());
            }));
        });
    }

    private Pass toApi(PassRecord record) {
        return new Pass(record.playerUuid(), record.seasonId(), record.passType(), record.purchaseTime());
    }
}
