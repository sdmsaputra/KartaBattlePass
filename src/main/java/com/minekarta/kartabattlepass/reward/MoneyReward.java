package com.minekarta.kartabattlepass.reward;

import com.minekarta.kartabattlepass.KartaBattlePass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.List;

public class MoneyReward extends Reward {
    private final double amount;

    public MoneyReward(String track, int level, String rewardId, double amount) {
        super(track, level, rewardId);
        this.amount = amount;
    }

    @Override
    public void give(Player player) {
        if (KartaBattlePass.getInstance().getVaultHook() != null && KartaBattlePass.getInstance().getVaultHook().getEconomy() != null) {
            KartaBattlePass.getInstance().getVaultHook().getEconomy().depositPlayer(player, amount);
        }
    }

    @Override
    public ItemStack getDisplayItem() {
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = item.getItemMeta();
        NumberFormat formatter = NumberFormat.getCurrencyInstance();


        meta.displayName(Component.text("Money Reward", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
                Component.text("Receive money upon claiming.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text(" "),
                Component.text("Amount: ", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(formatter.format(amount), NamedTextColor.WHITE))
        ));

        item.setItemMeta(meta);
        return item;
    }
}
