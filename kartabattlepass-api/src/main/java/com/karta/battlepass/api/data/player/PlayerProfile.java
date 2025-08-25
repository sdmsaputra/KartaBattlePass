package com.karta.battlepass.api.data.player;

import com.karta.battlepass.api.data.pass.PassType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A snapshot of a player's current battle pass status.
 * <p>
 * This record is immutable and represents the player's data at a specific moment.
 *
 * @param uuid The unique identifier of the player.
 * @param name The last known username of the player.
 * @param points The total number of battle pass points the player has.
 * @param tier The current tier the player has unlocked.
 * @param passType The type of pass the player currently has active for the season.
 */
public record PlayerProfile(
    @NotNull UUID uuid,
    @NotNull String name,
    long points,
    int tier,
    @NotNull PassType passType
) {
}
