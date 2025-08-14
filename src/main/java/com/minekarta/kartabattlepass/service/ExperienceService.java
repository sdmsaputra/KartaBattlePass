package com.minekarta.kartabattlepass.service;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.event.PlayerBattlePassLevelUpEvent;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ExperienceService {

    private final KartaBattlePass plugin;
    private final int maxLevel;
    private final int baseXp;

    public ExperienceService(KartaBattlePass plugin) {
        this.plugin = plugin;
        this.maxLevel = plugin.getConfig().getInt("battlepass.max-level", 100);
        this.baseXp = plugin.getConfig().getInt("battlepass.exp-per-level-base", 100);
    }

    public int getXpForLevel(int level) {
        return level * baseXp;
    }

    public void addXP(Player player, int amount) {
        BattlePassPlayer bpp = plugin.getBattlePassStorage().getBattlePassPlayer(player.getUniqueId());
        if (bpp == null || bpp.getLevel() >= maxLevel) {
            return; // Player data not loaded or max level reached
        }

        bpp.setExp(bpp.getExp() + amount);
        player.sendMessage(ChatColor.AQUA + "+ " + amount + " XP"); // Feedback message

        // Level up check
        int xpForNextLevel = getXpForLevel(bpp.getLevel());
        while (bpp.getExp() >= xpForNextLevel && bpp.getLevel() < maxLevel) {
            int oldLevel = bpp.getLevel();
            bpp.setLevel(oldLevel + 1);
            bpp.setExp(bpp.getExp() - xpForNextLevel);

            // Call level up event
            PlayerBattlePassLevelUpEvent event = new PlayerBattlePassLevelUpEvent(player, oldLevel, bpp.getLevel());
            plugin.getServer().getPluginManager().callEvent(event);

            // Send level up message
            player.sendMessage(ChatColor.GREEN + "Selamat! Kamu telah mencapai Battle Pass Level " + bpp.getLevel() + "!");

            // Handle rewards
            if (plugin.getConfig().getBoolean("rewards.auto-grant", false)) {
                plugin.getRewardService().claimLevelRewards(player, bpp.getLevel());
            } else {
                player.sendMessage(ChatColor.GOLD + "You have new rewards to claim! Type /bp rewards.");
            }

            xpForNextLevel = getXpForLevel(bpp.getLevel());
        }

        plugin.getBattlePassStorage().savePlayerData(player.getUniqueId(), true);
    }

    public void setXP(Player player, int amount) {
        BattlePassPlayer bpp = plugin.getBattlePassStorage().getBattlePassPlayer(player.getUniqueId());
        if (bpp == null) {
            return; // Player data not loaded
        }
        bpp.setExp(amount);
        // No level-up check on setXP, as it's for setting an exact value.
        // This can be changed if needed.
        plugin.getBattlePassStorage().savePlayerData(player.getUniqueId(), true);
    }

    public void setLevel(Player player, int level) {
        BattlePassPlayer bpp = plugin.getBattlePassStorage().getBattlePassPlayer(player.getUniqueId());
        if (bpp == null) {
            return; // Player data not loaded
        }
        if (level > maxLevel) {
            level = maxLevel;
        }
        if (level < 0) {
            level = 0;
        }

        int oldLevel = bpp.getLevel();
        bpp.setLevel(level);
        bpp.setExp(0); // Reset XP to 0 when level is set manually

        // Fire level up events if the level increased
        if (bpp.getLevel() > oldLevel) {
            for (int i = oldLevel + 1; i <= bpp.getLevel(); i++) {
                PlayerBattlePassLevelUpEvent event = new PlayerBattlePassLevelUpEvent(player, i - 1, i);
                plugin.getServer().getPluginManager().callEvent(event);
            }
        }

        plugin.getBattlePassStorage().savePlayerData(player.getUniqueId(), true);
    }
}
