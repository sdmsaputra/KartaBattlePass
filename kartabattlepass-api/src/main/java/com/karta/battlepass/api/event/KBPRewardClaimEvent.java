package com.karta.battlepass.api.event;

import com.karta.battlepass.api.data.pass.PassType;
import com.karta.battlepass.api.data.reward.Reward;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Called when a player claims the rewards for a specific tier.
 */
public class KBPRewardClaimEvent extends KartaBattlePassEvent {
    private final Player player;
    private final int tierLevel;
    private final PassType passType;
    private final List<Reward> rewards;

    public KBPRewardClaimEvent(@NotNull Player player, int tierLevel, @NotNull PassType passType, @NotNull List<Reward> rewards) {
        this.player = player;
        this.tierLevel = tierLevel;
        this.passType = passType;
        this.rewards = rewards;
    }

    /**
     * Gets the player who is claiming the rewards.
     * @return The player.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the tier level being claimed.
     * @return The tier level.
     */
    public int getTierLevel() {
        return tierLevel;
    }

    /**
     * Gets the pass track (FREE or PREMIUM) for which the rewards are being claimed.
     * @return The pass type.
     */
    @NotNull
    public PassType getPassType() {
        return passType;
    }

    /**
     * Gets the list of rewards being claimed.
     * @return An unmodifiable list of rewards.
     */
    @NotNull
    public List<Reward> getRewards() {
        return rewards;
    }
}
