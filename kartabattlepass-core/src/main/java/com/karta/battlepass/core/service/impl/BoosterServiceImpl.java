package com.karta.battlepass.core.service.impl;

import com.karta.battlepass.api.data.booster.ActiveBooster;
import com.karta.battlepass.api.data.booster.Booster;
import com.karta.battlepass.api.data.booster.BoosterType;
import com.karta.battlepass.api.service.BoosterService;
import com.karta.battlepass.core.service.ServiceRegistry;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BoosterServiceImpl implements BoosterService {

    private final ServiceRegistry registry;

    public BoosterServiceImpl(ServiceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Optional<Booster> getBoosterById(@NotNull String boosterId) {
        // TODO: Implement logic
        return Optional.empty();
    }

    @Override
    public @NotNull CompletableFuture<ActiveBooster> activateBooster(
            @NotNull String boosterId, @Nullable UUID playerUuid) {
        // TODO: Implement logic
        return null;
    }

    @Override
    public @NotNull CompletableFuture<Void> deactivateBooster(
            @NotNull ActiveBooster activeBooster) {
        // TODO: Implement logic
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public @NotNull CompletableFuture<List<ActiveBooster>> getActiveBoosters(
            @NotNull UUID playerUuid) {
        // TODO: Implement logic
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public @NotNull CompletableFuture<Double> getMultiplier(
            @NotNull UUID playerUuid, @NotNull BoosterType type) {
        // TODO: Implement logic
        return CompletableFuture.completedFuture(1.0);
    }
}
