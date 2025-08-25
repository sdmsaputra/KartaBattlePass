package com.karta.battlepass.bukkit.gui;

import java.util.Arrays;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public abstract class Menu implements InventoryHolder {

    protected final Inventory inventory;
    protected final Player player;
    protected static final MiniMessage mm = MiniMessage.miniMessage();

    public Menu(Player player, int size, Component title) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    public abstract void handleMenu(InventoryClickEvent event);

    public void open() {
        player.openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    protected ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(mm.deserialize(name));
        meta.lore(Arrays.stream(lore).map(mm::deserialize).collect(Collectors.toList()));
        item.setItemMeta(meta);
        return item;
    }
}
