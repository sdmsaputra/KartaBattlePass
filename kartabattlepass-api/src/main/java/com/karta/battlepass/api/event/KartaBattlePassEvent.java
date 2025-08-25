package com.karta.battlepass.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * A base for all KartaBattlePass custom events.
 */
public abstract class KartaBattlePassEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public KartaBattlePassEvent() {
        super(false); // All events are sync by default unless specified otherwise
    }

    public KartaBattlePassEvent(boolean isAsync) {
        super(isAsync);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
