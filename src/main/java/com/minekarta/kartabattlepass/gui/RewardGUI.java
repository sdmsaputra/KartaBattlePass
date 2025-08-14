package com.minekarta.kartabattlepass.gui;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import com.minekarta.kartabattlepass.reward.Reward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RewardGUI {
    private final KartaBattlePass plugin;
    private final Player player;
    private final BattlePassPlayer bpp;
    private final MiniMessage miniMessage;
    private int page;

    private static final int FREE_SLOTS_PER_PAGE = 18; // Rows 1-2 (slots 9-26)
    private static final int PREMIUM_SLOTS_PER_PAGE = 18; // Rows 4-5 (slots 36-53)

    public RewardGUI(KartaBattlePass plugin, Player player, int page) {
        this.plugin = plugin;
        this.player = player;
        this.bpp = plugin.getBattlePassStorage().getPlayerData(player.getUniqueId());
        this.miniMessage = plugin.getMiniMessage();
        this.page = page;
    }

    public void open() {
        if (bpp == null) {
            player.sendMessage(Component.text("Your data is not loaded.", NamedTextColor.RED));
            return;
        }

        ConfigurationSection guiConfig = plugin.getConfig().getConfigurationSection("gui.rewards");
        String title = guiConfig.getString("title", "Battle Pass Rewards");
        int size = guiConfig.getInt("size", 54);

        Inventory gui = Bukkit.createInventory(player, size, miniMessage.deserialize(title + " - Page " + (page + 1)));

        populateGui(gui, guiConfig);

        player.openInventory(gui);
    }

    private void populateGui(Inventory gui, ConfigurationSection guiConfig) {
        // Get and sort all rewards
        List<Reward> allRewards = plugin.getRewardService().getAllRewards().stream()
                .sorted(Comparator.comparingInt(Reward::getLevel))
                .collect(Collectors.toList());

        List<Reward> freeRewards = allRewards.stream().filter(r -> !r.isPremium()).collect(Collectors.toList());
        List<Reward> premiumRewards = allRewards.stream().filter(Reward::isPremium).collect(Collectors.toList());

        int totalPages = Math.max(1, (int) Math.ceil((double) Math.max(freeRewards.size(), premiumRewards.size()) / FREE_SLOTS_PER_PAGE));
        if (page >= totalPages) page = totalPages - 1;

        // Separator
        Material separatorMat = Material.matchMaterial(guiConfig.getString("separator_item", "GRAY_STAINED_GLASS_PANE"));
        ItemStack separator = new ItemStack(separatorMat != null ? separatorMat : Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = separator.getItemMeta();
        meta.displayName(Component.text(" "));
        separator.setItemMeta(meta);
        for (int i = 27; i <= 35; i++) {
            gui.setItem(i, separator);
        }

        // Populate reward items for the current page
        addRewardItems(gui, freeRewards, 9, FREE_SLOTS_PER_PAGE);
        addRewardItems(gui, premiumRewards, 36, PREMIUM_SLOTS_PER_PAGE);

        // Navigation
        addNavigationControls(gui, totalPages);
    }

    private void addRewardItems(Inventory gui, List<Reward> rewards, int startSlot, int slotsPerPage) {
        int startIndex = page * slotsPerPage;
        for (int i = 0; i < slotsPerPage; i++) {
            int rewardIndex = startIndex + i;
            if (rewardIndex < rewards.size()) {
                Reward reward = rewards.get(rewardIndex);
                gui.setItem(startSlot + i, createRewardItem(reward));
            } else {
                 // Fill empty reward slots with a different pane
                ItemStack emptySlot = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta meta = emptySlot.getItemMeta();
                meta.displayName(Component.text(" "));
                emptySlot.setItemMeta(meta);
                gui.setItem(startSlot + i, emptySlot);
            }
        }
    }

    private void addNavigationControls(Inventory gui, int totalPages) {
        // Previous Page
        if (page > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta meta = prev.getItemMeta();
            meta.displayName(miniMessage.deserialize("<yellow>Previous Page"));
            prev.setItemMeta(meta);
            gui.setItem(45, prev);
        }

        // Page Info & Back Button
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.displayName(miniMessage.deserialize("<red>Back to Main Menu"));
        backMeta.lore(List.of(miniMessage.deserialize("<gray>Page " + (page + 1) + "/" + totalPages)));
        back.setItemMeta(backMeta);
        gui.setItem(49, back);

        // Next Page
        if (page < totalPages - 1) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta meta = next.getItemMeta();
            meta.displayName(miniMessage.deserialize("<yellow>Next Page"));
            next.setItemMeta(meta);
            gui.setItem(53, next);
        }
    }

    private ItemStack createRewardItem(Reward reward) {
        ItemStack item;
        boolean isClaimed = bpp.hasClaimedReward(reward.getLevel(), reward.getRewardId());
        boolean isUnlocked = bpp.getLevel() >= reward.getLevel();
        boolean hasPremiumAccess = player.hasPermission("kartabattlepass.premium");

        if (isClaimed) {
            item = new ItemStack(Material.HOPPER_MINECART);
        } else if (isUnlocked && (!reward.isPremium() || hasPremiumAccess)) {
            item = new ItemStack(Material.CHEST_MINECART);
        } else {
            item = new ItemStack(Material.FURNACE_MINECART);
        }

        ItemMeta meta = item.getItemMeta();
        ItemStack displayItem = reward.getDisplayItem();
        if (displayItem.hasItemMeta()) {
            ItemMeta displayMeta = displayItem.getItemMeta();
            if (displayMeta.hasDisplayName()) {
                meta.displayName(displayMeta.displayName());
            }
            if (displayMeta.hasLore()) {
                meta.lore(displayMeta.lore());
            }
        }

        List<Component> lore = meta.hasLore() ? new ArrayList<>(meta.lore()) : new ArrayList<>();
        lore.add(Component.text(" "));

        if (isClaimed) {
            lore.add(miniMessage.deserialize("<green>✔ Claimed</green>"));
            if (meta.hasDisplayName()) {
                meta.displayName(meta.displayName().decoration(net.kyori.adventure.text.format.TextDecoration.STRIKETHROUGH, true));
            }
        } else if (!isUnlocked) {
            lore.add(miniMessage.deserialize("<red>✖ Locked</red>"));
            lore.add(miniMessage.deserialize("<gray>Reach Level " + reward.getLevel() + " to unlock.</gray>"));
        } else if (reward.isPremium() && !hasPremiumAccess) {
            lore.add(miniMessage.deserialize("<red>✖ Premium Locked</red>"));
            lore.add(miniMessage.deserialize("<gray>Requires the Premium Battle Pass.</gray>"));
        } else {
            lore.add(miniMessage.deserialize("<yellow>▶ Click to Claim!</yellow>"));
        }

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
