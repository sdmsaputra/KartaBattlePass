package com.karta.battlepass.api.event;

import com.karta.battlepass.api.data.booster.ActiveBooster;
import org.jetbrains.annotations.NotNull;

/** Called when a booster is activated, either globally or for a player. */
public class KBPBoosterActivateEvent extends KartaBattlePassEvent {
    private final ActiveBooster activeBooster;

    public KBPBoosterActivateEvent(@NotNull final ActiveBooster activeBooster) {
        this.activeBooster = activeBooster;
    }

    /**
     * Gets the booster instance that was activated.
     *
     * @return The active booster.
     */
    @NotNull
    public ActiveBooster getActiveBooster() {
        return activeBooster;
    }
}
