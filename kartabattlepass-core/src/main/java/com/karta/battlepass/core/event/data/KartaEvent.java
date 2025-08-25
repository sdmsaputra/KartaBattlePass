package com.karta.battlepass.core.event.data;

import java.util.UUID;

/** A base marker interface for all internal event data. */
public interface KartaEvent {
    UUID playerUuid();
}
