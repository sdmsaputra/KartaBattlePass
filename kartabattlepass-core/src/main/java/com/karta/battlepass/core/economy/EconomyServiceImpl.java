package com.karta.battlepass.core.economy;

import com.karta.battlepass.api.economy.EconomyProvider;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public class EconomyServiceImpl implements EconomyService {

    private final Map<String, EconomyProvider> providers = new ConcurrentHashMap<>();
    private EconomyProvider activeProvider;

    @Override
    public void registerProvider(@NotNull EconomyProvider provider) {
        providers.put(provider.getName().toUpperCase(), provider);
    }

    @Override
    public boolean setActiveProvider(@NotNull String name) {
        EconomyProvider provider = providers.get(name.toUpperCase());
        if (provider != null) {
            this.activeProvider = provider;
            return true;
        }
        return false;
    }

    @Override
    public @NotNull Optional<EconomyProvider> getActiveProvider() {
        return Optional.ofNullable(activeProvider);
    }
}
