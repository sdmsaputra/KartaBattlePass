package com.karta.battlepass.core.event.data;

import java.util.UUID;

/**
 * A base marker record for all internal event data.
 */
public record KartaEvent(UUID playerUuid) {
}
