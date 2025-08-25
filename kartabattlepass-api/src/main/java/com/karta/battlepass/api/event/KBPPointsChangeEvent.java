package com.karta.battlepass.api.event;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/** Called when a player's battle pass points change. */
public class KBPPointsChangeEvent extends KartaBattlePassEvent {
    private final Player player;
    private final long oldPoints;
    private final long newPoints;

    public KBPPointsChangeEvent(
            @NotNull final Player player, final long oldPoints, final long newPoints) {
        super(true);
        this.player = player;
        this.oldPoints = oldPoints;
        this.newPoints = newPoints;
    }

    /**
     * Gets the player whose points changed.
     *
     * @return The player.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the player's points total before the change.
     *
     * @return The old points value.
     */
    public long getOldPoints() {
        return oldPoints;
    }

    /**
     * Gets the player's points total after the change.
     *
     * @return The new points value.
     */
    public long getNewPoints() {
        return newPoints;
    }
}
