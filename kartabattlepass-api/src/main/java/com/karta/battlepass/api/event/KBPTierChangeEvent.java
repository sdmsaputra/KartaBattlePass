package com.karta.battlepass.api.event;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player levels up to a new tier.
 */
public class KBPTierChangeEvent extends KartaBattlePassEvent {
    private final Player player;
    private final int oldTier;
    private final int newTier;

    public KBPTierChangeEvent(@NotNull Player player, int oldTier, int newTier) {
        super(true);
        this.player = player;
        this.oldTier = oldTier;
        this.newTier = newTier;
    }

    /**
     * Gets the player who changed tiers.
     * @return The player.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the player's tier before the change.
     * @return The old tier level.
     */
    public int getOldTier() {
        return oldTier;
    }

    /**
     * Gets the player's new tier.
     * @return The new tier level.
     */
    public int getNewTier() {
        return newTier;
    }
}
