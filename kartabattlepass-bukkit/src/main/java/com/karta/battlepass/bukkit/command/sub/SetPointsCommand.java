package com.karta.battlepass.bukkit.command.sub;

import com.karta.battlepass.api.service.PlayerService;
import com.karta.battlepass.bukkit.command.SubCommand;
import com.karta.battlepass.core.service.ServiceRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SetPointsCommand implements SubCommand {

    private final PlayerService playerService;

    public SetPointsCommand(ServiceRegistry registry) {
        this.playerService = registry.playerService();
    }

    @Override
    public @NotNull String getName() {
        return "setpoints";
    }

    @Override
    public @NotNull List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public String getPermission() {
        return "kbp.admin.setpoints";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Usage: /kbp setpoints <player> <amount>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found.");
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid amount.");
            return;
        }

        playerService.setPoints(target.getUniqueId(), amount).thenAccept(v -> {
            sender.sendMessage("Set " + target.getName() + "'s points to " + amount);
        });
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            return List.of("0", "100", "1000");
        }
        return Collections.emptyList();
    }
}
