package com.karta.battlepass.api.reward;

import com.karta.battlepass.api.data.reward.Reward;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * A service provider interface for defining custom reward logic.
 * <p>
 * Implement this interface to create a new type of reward that can be
 * registered with the {@link com.karta.battlepass.api.service.RewardService}.
 */
@FunctionalInterface
public interface RewardType {

    /**
     * Grants the reward to the specified player.
     *
     * @param player The player who will receive the reward.
     * @param reward The static definition of the reward, containing its configuration data.
     * @return A {@link CompletableFuture} that completes when the reward has been successfully given.
     */
    @NotNull
    CompletableFuture<Void> grant(@NotNull Player player, @NotNull Reward reward);

}
