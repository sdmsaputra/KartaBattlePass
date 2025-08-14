package com.minekarta.kartabattlepass.gui;

import com.minekarta.kartabattlepass.KartaBattlePass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class MainGUI {

    private final Inventory inventory;
    private final KartaBattlePass plugin;
    private final MiniMessage miniMessage;

    public MainGUI(KartaBattlePass plugin) {
        this.plugin = plugin;
        this.miniMessage = plugin.getMiniMessage();

        ConfigurationSection guiConfig = plugin.getConfig().getConfigurationSection("gui.main-menu");
        String title = guiConfig.getString("title", "KartaBattlePass");
        int size = guiConfig.getInt("size", 54);

        this.inventory = Bukkit.createInventory(null, size, miniMessage.deserialize(title));
        initializeItems(guiConfig);
    }

    private void initializeItems(ConfigurationSection guiConfig) {
        // Fill the background
        Material fillerMaterial = Material.matchMaterial(guiConfig.getString("filler-item", "GRAY_STAINED_GLASS_PANE"));
        if (fillerMaterial == null) fillerMaterial = Material.GRAY_STAINED_GLASS_PANE;
        ItemStack fillerItem = new ItemStack(fillerMaterial);
        ItemMeta fillerMeta = fillerItem.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.displayName(Component.text(" "));
            fillerItem.setItemMeta(fillerMeta);
        }
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, fillerItem);
        }

        // Create the main menu items from config
        ConfigurationSection itemsConfig = guiConfig.getConfigurationSection("items");
        if (itemsConfig != null) {
            for (String key : itemsConfig.getKeys(false)) {
                ConfigurationSection itemConfig = itemsConfig.getConfigurationSection(key);
                if (itemConfig != null) {
                    ItemStack item = createGuiItem(itemConfig);
                    int slot = itemConfig.getInt("slot", -1);
                    if (item != null && slot != -1) {
                        inventory.setItem(slot, item);
                    }
                }
            }
        }
    }

    private ItemStack createGuiItem(ConfigurationSection itemConfig) {
        String materialName = itemConfig.getString("material");
        if (materialName == null) return null;

        Material material = Material.matchMaterial(materialName);
        if (material == null) return null;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        String name = itemConfig.getString("name");
        if (name != null) {
            meta.displayName(miniMessage.deserialize(name));
        }

        List<String> loreLines = itemConfig.getStringList("lore");
        if (!loreLines.isEmpty()) {
            meta.lore(loreLines.stream()
                    .map(miniMessage::deserialize)
                    .collect(Collectors.toList()));
        }

        item.setItemMeta(meta);
        return item;
    }


    public void open(Player player) {
        player.openInventory(inventory);
    }
}
