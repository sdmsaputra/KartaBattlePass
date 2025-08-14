package com.minekarta.kartabattlepass.command;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.gui.MainGUI;
import com.minekarta.kartabattlepass.gui.RewardGUI;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KBPCommand implements TabExecutor {

    private final KartaBattlePass plugin;
    private final String NO_PERMISSION_MESSAGE = "§cKamu tidak memiliki izin untuk melakukan perintah ini!";

    public KBPCommand(KartaBattlePass plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            // By default, open the GUI for players, or show help for console
            if (sender instanceof Player) {
                return openMainGui((Player) sender);
            } else {
                return sendHelpMessage(sender);
            }
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "help":
                return sendHelpMessage(sender);
            case "open":
            case "rewards":
                if (sender instanceof Player) {
                    // This now opens the main menu, which has a rewards button.
                    return openMainGui((Player) sender);
                } else {
                    sender.sendMessage("This command can only be used by players.");
                    return true;
                }
            case "progress":
                return checkProgress(sender, args);
            case "reload":
                return reloadConfig(sender);
            case "setxp":
                return setXp(sender, args);
            case "addxp":
                return addXp(sender, args);
            case "setlevel":
                return setLevel(sender, args);
            default:
                sender.sendMessage(plugin.getMiniMessage().deserialize("<red>Unknown subcommand. Use /kbp help for a list of commands."));
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        final List<String> completions = new ArrayList<>();
        List<String> subcommands = new ArrayList<>(Arrays.asList("help", "rewards", "progress"));

        if (sender.hasPermission("kbattlepass.admin")) {
            subcommands.addAll(Arrays.asList("reload", "setxp", "addxp", "setlevel"));
        }

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], subcommands, completions);
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "progress":
                case "setxp":
                case "addxp":
                case "setlevel":
                    if (sender.hasPermission("kbattlepass.admin") || sender.hasPermission("kbattlepass.progress.others")) {
                        return StringUtil.copyPartialMatches(args[1], Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), new ArrayList<>());
                    }
                    break;
            }
        } else if (args.length == 3) {
             switch (args[0].toLowerCase()) {
                case "setxp":
                case "addxp":
                    return List.of("<amount>");
                case "setlevel":
                    return List.of("<level>");
            }
        }
        return completions;
    }

    private boolean sendHelpMessage(CommandSender sender) {
        sender.sendMessage(plugin.getMiniMessage().deserialize("<gold>--- KartaBattlePass Help ---"));
        sender.sendMessage(plugin.getMiniMessage().deserialize("<yellow>/kbp help <grey>- Shows this help message."));
        sender.sendMessage(plugin.getMiniMessage().deserialize("<yellow>/kbp rewards <grey>- Opens the Battle Pass rewards GUI."));
        sender.sendMessage(plugin.getMiniMessage().deserialize("<yellow>/kbp progress [player] <grey>- Checks your or another player's progress."));

        if (sender.hasPermission("kbattlepass.admin")) {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>--- Admin Commands ---"));
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>/kbp reload <grey>- Reloads the plugin configuration."));
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>/kbp setxp <player> <amount> <grey>- Sets a player's Battle Pass XP."));
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>/kbp addxp <player> <amount> <grey>- Adds Battle Pass XP to a player."));
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>/kbp setlevel <player> <level> <grey>- Sets a player's Battle Pass level."));
        }
        return true;
    }

    private boolean openMainGui(Player player) {
        if (!player.hasPermission("kbattlepass.open")) {
            player.sendMessage(Component.text(NO_PERMISSION_MESSAGE));
            return true;
        }
        new MainGUI(plugin).open(player);
        return true;
    }

    private boolean checkProgress(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player player) {
                showProgress(sender, player);
                return true;
            } else {
                sender.sendMessage(plugin.getMiniMessage().deserialize("<red>Usage: /kbp progress <player>"));
                return true;
            }
        }

        if (args.length >= 2) {
            if (!sender.hasPermission("kbattlepass.progress.others")) {
                sender.sendMessage(Component.text(NO_PERMISSION_MESSAGE));
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(plugin.getMiniMessage().deserialize("<red>Player not found: " + args[1]));
                return true;
            }
            showProgress(sender, target);
            return true;
        }
        return sendHelpMessage(sender); // Show help if usage is incorrect
    }

    private void showProgress(CommandSender sender, Player target) {
        BattlePassPlayer bpp = plugin.getBattlePassStorage().getBattlePassPlayer(target.getUniqueId());
        if (bpp == null) {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>Could not find Battle Pass data for " + target.getName()));
            return;
        }

        int currentLevel = bpp.getLevel();
        int currentXp = bpp.getExp();
        int xpForNextLevel = plugin.getExperienceService().getXpForLevel(currentLevel);
        int maxLevel = plugin.getConfig().getInt("battlepass.max-level", 100);

        sender.sendMessage(plugin.getMiniMessage().deserialize("<gold>--- Battle Pass Progress: " + target.getName() + " ---"));
        sender.sendMessage(plugin.getMiniMessage().deserialize("<yellow>Level: <white>" + currentLevel));

        if (currentLevel >= maxLevel) {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<green>Max level reached!"));
        } else {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<yellow>XP Progress: <white>" + currentXp + " / " + xpForNextLevel));
        }
    }

    private boolean reloadConfig(CommandSender sender) {
        if (!sender.hasPermission("kbattlepass.admin")) {
            sender.sendMessage(Component.text(NO_PERMISSION_MESSAGE));
            return true;
        }
        plugin.reload();
        sender.sendMessage(plugin.getMiniMessage().deserialize("§aBerhasil memuat ulang semua konfigurasi & data dari file."));
        return true;
    }

    private boolean setXp(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kbattlepass.admin")) {
            sender.sendMessage(Component.text(NO_PERMISSION_MESSAGE));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>Usage: /kbp setxp <player> <amount>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>Player '" + args[1] + "' not found."));
            return true;
        }

        try {
            int amount = Integer.parseInt(args[2]);
            plugin.getExperienceService().setXP(target, amount);
            sender.sendMessage(plugin.getMiniMessage().deserialize("§aBerhasil mengatur jumlah XP menjadi §e" + amount + " §auntuk §b" + target.getName() + "§a."));
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>The amount must be a number."));
        }
        return true;
    }

    private boolean addXp(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kbattlepass.admin")) {
            sender.sendMessage(Component.text(NO_PERMISSION_MESSAGE));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>Usage: /kbp addxp <player> <amount>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>Player '" + args[1] + "' not found."));
            return true;
        }

        try {
            int amount = Integer.parseInt(args[2]);
            plugin.getExperienceService().addXP(target, amount);
            sender.sendMessage(plugin.getMiniMessage().deserialize("§aBerhasil menambahkan §e" + amount + " XP §akepada §b" + target.getName() + "§a."));
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>The amount must be a number."));
        }
        return true;
    }

    private boolean setLevel(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kbattlepass.admin")) {
            sender.sendMessage(Component.text(NO_PERMISSION_MESSAGE));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>Usage: /kbp setlevel <player> <level>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>Player '" + args[1] + "' not found."));
            return true;
        }

        try {
            int level = Integer.parseInt(args[2]);
            plugin.getExperienceService().setLevel(target, level);
            sender.sendMessage(plugin.getMiniMessage().deserialize("§aBerhasil mengatur level §b" + target.getName() + " §amenjadi §e" + level + "§a."));
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMiniMessage().deserialize("<red>The level must be a number."));
        }
        return true;
    }
}
