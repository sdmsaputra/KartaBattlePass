package com.karta.battlepass.bukkit.command;

import com.karta.battlepass.core.service.ServiceRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.karta.battlepass.bukkit.command.sub.ReloadCommand;
import com.karta.battlepass.bukkit.command.sub.SetPointsCommand;
import com.karta.battlepass.core.service.ServiceRegistry;
import org.bukkit.command.Command;
import com.karta.battlepass.bukkit.gui.MainGui;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KbpCommand implements CommandExecutor, TabCompleter {

    private final ServiceRegistry registry;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public KbpCommand(ServiceRegistry registry) {
        this.registry = registry;
        registerSubCommands();
    }

    private void registerSubCommands() {
        registerSubCommand(new ReloadCommand(registry));
        registerSubCommand(new SetPointsCommand(registry));
    }

    private void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
        for (String alias : subCommand.getAliases()) {
            subCommands.put(alias.toLowerCase(), subCommand);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be run by a player.");
                return true;
            }
            new MainGui(player, registry.playerService());
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(subCommandName);

        if (subCommand == null) {
            sender.sendMessage("Unknown subcommand. Use /kbp help.");
            return true;
        }

        if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return subCommands.keySet().stream()
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .filter(name -> {
                        SubCommand sub = subCommands.get(name);
                        return sub.getPermission() == null || sender.hasPermission(sub.getPermission());
                    })
                    .collect(Collectors.toList());
        }

        if (args.length > 1) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) {
                    return subCommand.onTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
                }
            }
        }

        return new ArrayList<>();
    }
}
