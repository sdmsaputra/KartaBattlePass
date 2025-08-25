package com.karta.battlepass.core.event.bus;

import com.karta.battlepass.core.event.data.KartaEvent;
import org.jetbrains.annotations.NotNull;

/** An internal event bus for decoupling services and firing external events. */
public interface EventBus {

    /**
     * Fires an event. The implementation will handle both internal listeners (for
     * service-to-service communication) and firing external Bukkit events.
     *
     * @param event The event data to fire.
     */
    void fire(@NotNull KartaEvent event);
}
