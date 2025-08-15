package com.minekarta.kartabattlepass.gui;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import com.minekarta.kartabattlepass.quest.QuestCategory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuestCategoryGUI implements InventoryHolder {
    private final KartaBattlePass plugin;
    private final MiniMessage miniMessage;
    private final Player viewer;
    private Inventory inventory;

    public QuestCategoryGUI(KartaBattlePass plugin, Player viewer) {
        this.plugin = plugin;
        this.miniMessage = plugin.getMiniMessage();
        this.viewer = viewer;
        createInventory();
    }

    private void createInventory() {
        // TODO: Make title and size configurable
        this.inventory = Bukkit.createInventory(this, 54, miniMessage.deserialize("<blue>Quest Categories"));
        initializeItems();
    }

    private void initializeItems() {
        Map<String, QuestCategory> categories = plugin.getQuestConfig().getQuestCategories();
        BattlePassPlayer bpPlayer = plugin.getBattlePassStorage().getPlayerData(viewer.getUniqueId());

        if (bpPlayer == null) {
            viewer.sendMessage(Component.text("Your player data is not loaded correctly."));
            return;
        }

        int slot = 0;
        for (QuestCategory category : categories.values()) {
            if (slot >= 45) break; // Limit to 45 categories for one page

            ItemStack item = createCategoryItem(category, bpPlayer);
            inventory.setItem(slot, item);
            slot++;
        }

        // Add back button
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.displayName(miniMessage.deserialize("<red>Back to Main Menu"));
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton);
    }

    private ItemStack createCategoryItem(QuestCategory category, BattlePassPlayer bpPlayer) {
        Material material = Material.matchMaterial(category.getDisplayItem());
        if (material == null) {
            material = Material.STONE;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(miniMessage.deserialize(category.getDisplayName()));

        List<String> lore = new ArrayList<>();
        int progressIndex = bpPlayer.getCategoryProgress(category.getId());
        int totalQuests = category.getQuestIds().size();

        lore.add("<gray>Progress: <green>" + progressIndex + " / " + totalQuests);
        lore.add(" ");
        if (category.isCompleted(progressIndex)) {
            lore.add("<green>✔ Category Completed!");
        } else {
            lore.add("<yellow>Click to view quests!");
        }

        meta.lore(lore.stream().map(miniMessage::deserialize).collect(Collectors.toList()));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open() {
        viewer.openInventory(inventory);
    }
}
