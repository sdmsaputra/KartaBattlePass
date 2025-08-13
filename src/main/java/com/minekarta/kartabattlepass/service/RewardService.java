package com.minekarta.kartabattlepass.service;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class RewardService {

    private final KartaBattlePass plugin;

    public RewardService(KartaBattlePass plugin) {
        this.plugin = plugin;
    }

    public void claimReward(Player player, int level) {
        BattlePassPlayer bpp = plugin.getBattlePassStorage().getBattlePassPlayer(player.getUniqueId());
        if (bpp == null) {
            player.sendMessage(ChatColor.RED + "Data Battle Pass kamu tidak ditemukan.");
            return;
        }

        if (bpp.getLevel() < level) {
            player.sendMessage(ChatColor.RED + "Kamu belum mencapai level " + level + ".");
            return;
        }

        ConfigurationSection rewardsSection = plugin.getConfig().getConfigurationSection("rewards." + level);
        if (rewardsSection == null) {
            player.sendMessage(ChatColor.RED + "Tidak ada hadiah yang dikonfigurasi untuk level " + level + ".");
            return;
        }

        boolean claimedSomething = false;

        // Claim Free Rewards
        if (rewardsSection.isList("free")) {
            String claimKey = level + ":free";
            if (!bpp.getClaimedRewards().contains(claimKey)) {
                List<String> commands = rewardsSection.getStringList("free");
                executeCommands(player, commands);
                bpp.getClaimedRewards().add(claimKey);
                player.sendMessage(ChatColor.GREEN + "Kamu berhasil mengklaim hadiah gratis level " + level + "!");
                claimedSomething = true;
            }
        }

        // Claim Premium Rewards
        if (rewardsSection.isList("premium")) {
            if (player.hasPermission("kartabattlepass.premium")) {
                String claimKey = level + ":premium";
                if (!bpp.getClaimedRewards().contains(claimKey)) {
                    List<String> commands = rewardsSection.getStringList("premium");
                    executeCommands(player, commands);
                    bpp.getClaimedRewards().add(claimKey);
                    player.sendMessage(ChatColor.GOLD + "Kamu berhasil mengklaim hadiah premium level " + level + "!");
                    claimedSomething = true;
                }
            } else {
                // Optional: message if they could claim premium but don't have permission
                 player.sendMessage(ChatColor.YELLOW + "Kamu tidak memiliki akses premium untuk mengklaim hadiah ini.");
            }
        }

        if (claimedSomething) {
            plugin.getBattlePassStorage().savePlayerData(player.getUniqueId(), true);
        } else {
            player.sendMessage(ChatColor.YELLOW + "Semua hadiah untuk level " + level + " sudah kamu klaim.");
        }
    }

    private void executeCommands(OfflinePlayer player, List<String> commands) {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        for (String command : commands) {
            String processedCommand = command.replace("%player%", player.getName());
            Bukkit.dispatchCommand(console, processedCommand);
        }
    }
}
