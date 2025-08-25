package com.karta.battlepass.core.economy;

import com.karta.battlepass.api.economy.EconomyProvider;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/** A service to manage economy providers. */
public interface EconomyService {

    /**
     * Registers a new economy provider.
     *
     * @param provider The provider to register.
     */
    void registerProvider(@NotNull EconomyProvider provider);

    /**
     * Sets the active economy provider by name.
     *
     * @param name The name of the provider to set as active.
     * @return true if the provider was found and set, false otherwise.
     */
    boolean setActiveProvider(@NotNull String name);

    /**
     * Gets the currently active economy provider.
     *
     * @return An Optional containing the active provider, or empty if none is active.
     */
    @NotNull
    Optional<EconomyProvider> getActiveProvider();
}
