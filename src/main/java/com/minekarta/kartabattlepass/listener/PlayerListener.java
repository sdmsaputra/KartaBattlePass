package com.minekarta.kartabattlepass.listener;

import com.minekarta.kartabattlepass.KartaBattlePass;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final KartaBattlePass plugin;

    public PlayerListener(KartaBattlePass plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getBattlePassStorage().loadPlayerData(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getBattlePassStorage().unloadPlayerData(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (killer == null) {
            return; // Not killed by a player
        }

        String mobType = entity.getType().name();
        int xpAmount = plugin.getConfig().getInt("xp-sources.mob-kills." + mobType, 0);

        if (xpAmount > 0) {
            plugin.getExperienceService().addXP(killer, xpAmount);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        String blockType = block.getType().name();
        int xpAmount = plugin.getConfig().getInt("xp-sources.mining." + blockType, 0);

        if (xpAmount > 0) {
            plugin.getExperienceService().addXP(player, xpAmount);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Player player = event.getPlayer();
            int xpAmount = plugin.getConfig().getInt("xp-sources.fishing", 0);

            if (xpAmount > 0) {
                plugin.getExperienceService().addXP(player, xpAmount);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            String itemType = event.getRecipe().getResult().getType().name();
            int xpAmount = plugin.getConfig().getInt("xp-sources.crafting." + itemType, 0);

            if (xpAmount > 0) {
                plugin.getExperienceService().addXP(player, xpAmount);
            }
        }
    }
}
