package com.minekarta.kartabattlepass.gui;

import com.minekarta.kartabattlepass.KartaBattlePass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MainGUI {

    private final Inventory inventory;
    private final KartaBattlePass plugin;

    public MainGUI(KartaBattlePass plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(null, 27, Component.text("KartaBattlePass"));
        initializeItems();
    }

    private void initializeItems() {
        // Create 3 placeholder items using MiniMessage for potential formatting
        inventory.setItem(11, createGuiItem(Material.DIAMOND_SWORD, "<gold>Quests", "<gray>View your active quests."));
        inventory.setItem(13, createGuiItem(Material.EXPERIENCE_BOTTLE, "<green>Rewards", "<gray>Claim your rewards."));
        inventory.setItem(15, createGuiItem(Material.BOOK, "<aqua>Season Info", "<gray>Info about the current season."));

        // Fill empty slots with glass panes
        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glassPane.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(" "));
            glassPane.setItemMeta(meta);
        }
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, glassPane);
            }
        }
    }

    private ItemStack createGuiItem(Material material, String name, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        MiniMessage mm = plugin.getMiniMessage();

        if (meta != null) {
            meta.displayName(mm.deserialize(name));
            meta.lore(
                Arrays.stream(loreLines)
                      .map(mm::deserialize)
                      .collect(Collectors.toList())
            );
            item.setItemMeta(meta);
        }
        return item;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }
}
