package com.minekarta.kartabattlepass.gui;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.leaderboard.LeaderboardEntry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardGUI {

    private final KartaBattlePass plugin;
    private final Player viewer;
    private final MiniMessage miniMessage;
    private int page;

    public LeaderboardGUI(KartaBattlePass plugin, Player viewer, int page) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.miniMessage = plugin.getMiniMessage();
        this.page = page;
    }

    public void open() {
        // For now, we assume the leaderboard data is up-to-date.
        // A more robust system might force an update or show cached data.
        List<LeaderboardEntry> leaderboard = plugin.getLeaderboardService().getLeaderboard();

        int size = 54; // 6 rows
        String title = "Leaderboard - Page " + (page + 1);
        Inventory gui = Bukkit.createInventory(viewer, size, miniMessage.deserialize(title));

        populateLeaderboard(gui, leaderboard);

        viewer.openInventory(gui);
    }

    private void populateLeaderboard(Inventory gui, List<LeaderboardEntry> leaderboard) {
        // Slots 10-16, 19-25, 28-34 (21 slots for players)
        int[] playerSlots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        };

        int startIndex = page * playerSlots.length;

        for (int i = 0; i < playerSlots.length; i++) {
            int entryIndex = startIndex + i;
            if (entryIndex < leaderboard.size()) {
                LeaderboardEntry entry = leaderboard.get(entryIndex);
                gui.setItem(playerSlots[i], createPlayerHead(entry));
            } else {
                // Fill empty reward slots with a different pane
                ItemStack emptySlot = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta meta = emptySlot.getItemMeta();
                meta.displayName(Component.text(" "));
                emptySlot.setItemMeta(meta);
                gui.setItem(playerSlots[i], emptySlot);
            }
        }

        addNavigationControls(gui, leaderboard.size(), playerSlots.length);
    }

    private void addNavigationControls(Inventory gui, int totalEntries, int entriesPerPage) {
        int totalPages = (int) Math.ceil((double) totalEntries / entriesPerPage);
        if (totalPages > 2) totalPages = 2; // Max 2 pages as requested

        // Previous Page
        if (page > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta meta = prev.getItemMeta();
            meta.displayName(miniMessage.deserialize("<yellow>Previous Page"));
            prev.setItemMeta(meta);
            gui.setItem(45, prev);
        }

        // Back Button
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.displayName(miniMessage.deserialize("<red>Back to Main Menu"));
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

    private ItemStack createPlayerHead(LeaderboardEntry entry) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        // Set player skin
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(entry.getPlayerName()));

        // Set display name and lore
        meta.displayName(miniMessage.deserialize("<gold>#" + entry.getRank() + " - " + entry.getPlayerName()));
        List<Component> lore = new ArrayList<>();
        lore.add(miniMessage.deserialize("<gray>--------------------"));
        lore.add(miniMessage.deserialize("<white>Level: <green>" + entry.getLevel()));
        lore.add(miniMessage.deserialize("<white>XP: <green>" + entry.getExp()));
        lore.add(miniMessage.deserialize("<gray>--------------------"));
        meta.lore(lore);

        head.setItemMeta(meta);
        return head;
    }
}
