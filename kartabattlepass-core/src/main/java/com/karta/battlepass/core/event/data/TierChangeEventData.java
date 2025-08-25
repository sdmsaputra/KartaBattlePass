package com.karta.battlepass.core.event.data;

import java.util.UUID;

public record TierChangeEventData(UUID playerUuid, int oldTier, int newTier)
        implements KartaEvent {}
