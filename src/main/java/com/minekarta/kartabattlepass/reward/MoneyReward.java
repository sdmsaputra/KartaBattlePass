package com.minekarta.kartabattlepass.reward;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.hooks.VaultHook;
import org.bukkit.entity.Player;

public class MoneyReward extends Reward {
    private final double amount;

    public MoneyReward(String track, double amount) {
        super(track);
        this.amount = amount;
    }

    @Override
    public void give(Player player) {
        if (KartaBattlePass.getInstance().getVaultHook() != null && KartaBattlePass.getInstance().getVaultHook().getEconomy() != null) {
            KartaBattlePass.getInstance().getVaultHook().getEconomy().depositPlayer(player, amount);
        }
    }
}
