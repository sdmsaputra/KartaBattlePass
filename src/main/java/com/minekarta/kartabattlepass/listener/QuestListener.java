package com.minekarta.kartabattlepass.listener;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.service.QuestService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class QuestListener implements Listener {

    private final QuestService questService;

    public QuestListener(KartaBattlePass plugin) {
        this.questService = plugin.getQuestService();
    }

    // --- Block Events ---

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String target = event.getBlock().getType().name();
        questService.progressQuest(player, "block-break", target, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        String target = event.getBlock().getType().name();
        questService.progressQuest(player, "block-place", target, 1);
    }

    // --- Mob & Player Kill Events ---

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            Entity victim = event.getEntity();

            if (victim instanceof Player) {
                questService.progressQuest(killer, "kill-player", victim.getName(), 1);
            } else {
                String mobType = victim.getType().name();
                questService.progressQuest(killer, "kill-mob", mobType, 1);
            }
        }
    }

    // --- Item & Crafting Events ---

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            String target = event.getRecipe().getResult().getType().name();
            int amount = event.getRecipe().getResult().getAmount();
            questService.progressQuest(player, "craft", target, amount);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Player player = event.getPlayer();
            if (event.getCaught() != null) {
                String target = event.getCaught().getType().name();
                 questService.progressQuest(player, "fish", target, 1);
            } else {
                 questService.progressQuest(player, "fish", null, 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        String target = event.getItem().getType().name();
        questService.progressQuest(player, "enchant", target, 1);
        questService.progressQuest(player, "enchant-all", target, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFurnaceExtract(FurnaceExtractEvent event) {
        Player player = event.getPlayer();
        String target = event.getItemType().name();
        int amount = event.getItemAmount();
        questService.progressQuest(player, "smelt", target, amount);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        String target = event.getItem().getType().name();
        questService.progressQuest(player, "consume", target, 1);
    }


    // --- Player Action Events ---

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        questService.progressQuest(player, "chat", event.getMessage(), 1);
        String strippedMessage = event.getMessage().replaceAll("(?i)&[0-9A-FK-OR]", "");
        questService.progressQuest(player, "chat-stripped", strippedMessage, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        questService.progressQuest(player, "execute-command", event.getMessage(), 1);
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityTame(EntityTameEvent event) {
        if (event.getOwner() instanceof Player) {
            Player player = (Player) event.getOwner();
            String target = event.getEntity().getType().name();
            questService.progressQuest(player, "tame", target, 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        String target = event.getEntity().getType().name();
        questService.progressQuest(player, "shear", target, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityBreed(EntityBreedEvent event) {
        if(event.getBreeder() instanceof Player){
            Player player = (Player) event.getBreeder();
            String child = event.getEntity().getType().name();
            questService.progressQuest(player, "breed", child, 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().name().contains("RIGHT_CLICK")) {
            questService.progressQuest(player, "right-click", null, 1);
            if (event.hasBlock()) {
                questService.progressQuest(player, "right-click-block", event.getClickedBlock().getType().name(), 1);
            }
        } else if (event.getAction().name().contains("LEFT_CLICK")) {
            questService.progressQuest(player, "left-click", null, 1);
            if (event.hasBlock()) {
                questService.progressQuest(player, "left-click-block", event.getClickedBlock().getType().name(), 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.hasChangedBlock()) {
            Player player = event.getPlayer();
            questService.progressQuest(player, "move", null, 1);
            if (player.isSwimming()) {
                questService.progressQuest(player, "swim", null, 1);
            }
            if (player.isSprinting()) {
                questService.progressQuest(player, "sprint", null, 1);
            }
            if (player.isSneaking()) {
                questService.progressQuest(player, "sneak", null, 1);
            }
            if (player.isGliding()) {
                questService.progressQuest(player, "glide", null, 1);
            }
            if (player.isFlying()) {
                questService.progressQuest(player, "fly", null, 1);
            }
        }
    }
}
