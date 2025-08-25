package com.karta.battlepass.api.event;

import com.karta.battlepass.api.data.booster.ActiveBooster;
import org.jetbrains.annotations.NotNull;

/** Called when an active booster expires. */
public class KBPBoosterExpireEvent extends KartaBattlePassEvent {
    private final ActiveBooster activeBooster;

    public KBPBoosterExpireEvent(@NotNull final ActiveBooster activeBooster) {
        this.activeBooster = activeBooster;
    }

    /**
     * Gets the booster instance that expired.
     *
     * @return The expired booster.
     */
    @NotNull
    public ActiveBooster getActiveBooster() {
        return activeBooster;
    }
}
