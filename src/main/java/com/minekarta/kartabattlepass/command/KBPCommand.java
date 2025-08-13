package com.minekarta.kartabattlepass.command;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KBPCommand implements CommandExecutor {

    private final KartaBattlePass plugin;

    public KBPCommand(KartaBattlePass plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            // Can be used to show a help menu or open a GUI in the future.
            sender.sendMessage(ChatColor.GOLD + "--- KartaBattlePass ---");
            sender.sendMessage(ChatColor.YELLOW + "/bp progress - Lihat progres Battle Pass Anda.");
            sender.sendMessage(ChatColor.YELLOW + "/bp claim <level> - Klaim hadiah level.");
            if (sender.hasPermission("kartabattlepass.admin")) {
                sender.sendMessage(ChatColor.RED + "/bp reload - Reload konfigurasi.");
            }
            return true;
        }

        String subCommand = args[0].toLowerCase();

        // Admin commands that can be run from console
        if (subCommand.equals("reload")) {
            return reloadConfig(sender);
        }
        if (subCommand.equals("givexp")) {
            return giveXp(sender, args);
        }

        // Player-only commands
        if (sender instanceof Player player) {
            switch (subCommand) {
                case "progress":
                    return showProgress(player);
                case "claim":
                    return claimReward(player, args);
                default:
                    player.sendMessage(ChatColor.RED + "Perintah tidak dikenal. Gunakan /bp untuk bantuan.");
                    return true;
            }
        } else {
            sender.sendMessage("Perintah '" + subCommand + "' hanya bisa digunakan oleh pemain.");
            return true;
        }
    }

    private boolean showProgress(Player player) {
        BattlePassPlayer bpp = plugin.getBattlePassStorage().getBattlePassPlayer(player.getUniqueId());
        if (bpp == null) {
            player.sendMessage(ChatColor.RED + "Data Battle Pass Anda tidak dapat ditemukan.");
            return true;
        }

        int currentLevel = bpp.getLevel();
        int currentXp = bpp.getExp();
        int xpForNextLevel = plugin.getExperienceService().getXpForLevel(currentLevel);
        int maxLevel = plugin.getConfig().getInt("battlepass.max-level", 100);

        player.sendMessage(ChatColor.GOLD + "--- Progress Battle Pass ---");
        player.sendMessage(ChatColor.YELLOW + "Level: " + ChatColor.WHITE + currentLevel);

        if (currentLevel >= maxLevel) {
            player.sendMessage(ChatColor.GREEN + "Anda telah mencapai level maksimal!");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Progress XP: " + ChatColor.WHITE + currentXp + " / " + xpForNextLevel);
            player.sendMessage(ChatColor.YELLOW + "Bar: " + getProgressBar(currentXp, xpForNextLevel));
        }
        return true;
    }

    private String getProgressBar(int current, int max, int totalBars, char symbol, ChatColor completedColor, ChatColor notCompletedColor) {
        if (max == 0) return ""; // Avoid division by zero
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return completedColor + new String(new char[progressBars]).replace('\0', symbol) +
                notCompletedColor + new String(new char[totalBars - progressBars]).replace('\0', symbol);
    }

    private String getProgressBar(int current, int max) {
        return getProgressBar(current, max, 20, '|', ChatColor.GREEN, ChatColor.GRAY);
    }

    private boolean claimReward(Player player, String[] args) {
        if (!player.hasPermission("kartabattlepass.claim")) {
            player.sendMessage(ChatColor.RED + "Anda tidak memiliki izin untuk mengklaim hadiah.");
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Penggunaan: /bp claim <level>");
            return true;
        }

        try {
            int level = Integer.parseInt(args[1]);
            plugin.getRewardService().claimReward(player, level);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Level harus berupa angka.");
        }
        return true;
    }

    private boolean reloadConfig(CommandSender sender) {
        if (!sender.hasPermission("kartabattlepass.admin")) {
            sender.sendMessage(ChatColor.RED + "Anda tidak memiliki izin untuk melakukan ini.");
            return true;
        }
        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "Konfigurasi KartaBattlePass berhasil di-reload.");
        return true;
    }

    private boolean giveXp(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kartabattlepass.admin")) {
            sender.sendMessage(ChatColor.RED + "Anda tidak memiliki izin untuk melakukan ini.");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Penggunaan: /bp givexp <player> <amount>");
            return true;
        }

        Player target = org.bukkit.Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Pemain '" + args[1] + "' tidak ditemukan atau sedang offline.");
            return true;
        }

        try {
            int amount = Integer.parseInt(args[2]);
            plugin.getExperienceService().addXP(target, amount);
            sender.sendMessage(ChatColor.GREEN + "Berhasil memberikan " + amount + " XP kepada " + target.getName() + ".");
            target.sendMessage(ChatColor.GREEN + "Anda menerima " + amount + " XP dari admin.");
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Jumlah XP harus berupa angka.");
        }
        return true;
    }
}
