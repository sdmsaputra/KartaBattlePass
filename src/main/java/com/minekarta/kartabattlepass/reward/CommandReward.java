package com.minekarta.kartabattlepass.reward;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CommandReward extends Reward {
    private final String command;
    private final String executor;

    public CommandReward(String track, int level, String rewardId, String command, String executor) {
        super(track, level, rewardId);
        this.command = command;
        this.executor = executor != null ? executor : "console";
    }

    @Override
    public void give(Player player) {
        String processedCommand = command.replace("%player%", player.getName());
        if ("player".equalsIgnoreCase(executor)) {
            player.performCommand(processedCommand);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
        }
    }

    @Override
    public ItemStack getDisplayItem() {
        ItemStack item = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Command Reward", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
                Component.text("Executes a command upon claiming.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text(" "),
                Component.text("Command: ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(command, NamedTextColor.WHITE)),
                Component.text("Executor: ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(executor, NamedTextColor.WHITE))
        ));

        item.setItemMeta(meta);
        return item;
    }
}
