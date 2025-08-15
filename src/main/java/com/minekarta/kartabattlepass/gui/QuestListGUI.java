package com.minekarta.kartabattlepass.gui;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import com.minekarta.kartabattlepass.quest.PlayerQuestProgress;
import com.minekarta.kartabattlepass.quest.Quest;
import com.minekarta.kartabattlepass.quest.QuestCategory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuestListGUI implements InventoryHolder {

    private final KartaBattlePass plugin;
    private final MiniMessage miniMessage;
    private final Player viewer;
    private final QuestCategory category;
    private Inventory inventory;

    public QuestListGUI(KartaBattlePass plugin, Player viewer, QuestCategory category) {
        this.plugin = plugin;
        this.miniMessage = plugin.getMiniMessage();
        this.viewer = viewer;
        this.category = category;
        createInventory();
    }

    private void createInventory() {
        // TODO: Make size configurable, maybe based on quest list size
        this.inventory = Bukkit.createInventory(this, 54, miniMessage.deserialize(category.getDisplayName()));
        initializeItems();
    }

    private void initializeItems() {
        BattlePassPlayer bpPlayer = plugin.getBattlePassStorage().getPlayerData(viewer.getUniqueId());
        if (bpPlayer == null) {
            viewer.sendMessage(Component.text("Your player data is not loaded correctly."));
            return;
        }

        int playerProgressIndex = bpPlayer.getCategoryProgress(category.getId());

        List<String> questIds = category.getQuestIds();
        for (int i = 0; i < questIds.size(); i++) {
            if (i >= 45) break; // Limit quests displayed

            String questId = questIds.get(i);
            Quest quest = plugin.getQuestConfig().getQuest(questId);
            if (quest == null) continue;

            ItemStack item;
            if (i < playerProgressIndex) {
                // Completed quest
                item = createQuestItem(quest, Material.GREEN_STAINED_GLASS_PANE, "&a&l✔ &a" + quest.getDisplayName());
            } else if (i == playerProgressIndex) {
                // Current quest
                item = createQuestItem(quest, Material.YELLOW_STAINED_GLASS_PANE, "&e&l► &e" + quest.getDisplayName());
                addProgressLore(item, bpPlayer.getQuestProgress(questId), quest.getAmount());
            } else {
                // Locked quest
                item = createQuestItem(quest, Material.GRAY_STAINED_GLASS_PANE, "&7&l✖ &7" + quest.getDisplayName());
            }
            inventory.setItem(i, item);
        }

        // Add back button
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.displayName(miniMessage.deserialize("<red>Back to Categories"));
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton);
    }

    private ItemStack createQuestItem(Quest quest, Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(miniMessage.deserialize(displayName));
        item.setItemMeta(meta);
        return item;
    }

    private void addProgressLore(ItemStack item, PlayerQuestProgress progress, int requiredAmount) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        List<String> lore = new ArrayList<>();
        int currentAmount = (progress != null) ? progress.getCurrentAmount() : 0;

        lore.add("<gray>Progress: <yellow>" + currentAmount + " / " + requiredAmount);
        meta.lore(lore.stream().map(miniMessage::deserialize).collect(Collectors.toList()));
        item.setItemMeta(meta);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open() {
        viewer.openInventory(inventory);
    }

    public QuestCategory getCategory() {
        return category;
    }
}
