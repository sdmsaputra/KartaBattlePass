package com.minekarta.kartabattlepass.command;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.gui.MainGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KBPCommand implements CommandExecutor {

    private final KartaBattlePass plugin;

    public KBPCommand(KartaBattlePass plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            // For now, we send a simple hardcoded message.
            // A proper message handler can be added later.
            sender.sendMessage(Component.text("This command can only be used by players."));
            return true;
        }

        new MainGUI(plugin).open(player);
        return true;
    }
}
