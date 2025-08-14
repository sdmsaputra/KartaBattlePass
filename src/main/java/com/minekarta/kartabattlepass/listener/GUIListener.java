package com.minekarta.kartabattlepass.listener;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {

    private final KartaBattlePass plugin;

    public GUIListener(KartaBattlePass plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Battle Pass Rewards")) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int clickedSlot = event.getSlot();
        int level = clickedSlot + 1;

        BattlePassPlayer bpp = plugin.getBattlePassStorage().getBattlePassPlayer(player.getUniqueId());
        if (bpp == null) {
            return;
        }

        if (bpp.getLevel() >= level) {
            // Player has reached this level, attempt to claim
            player.closeInventory(); // Close GUI to prevent spam clicking
            plugin.getRewardService().claimLevelRewards(player, level);
        } else {
            // Level not yet reached
            player.sendMessage(ChatColor.RED + "You have not unlocked this level yet!");
        }
    }
}
