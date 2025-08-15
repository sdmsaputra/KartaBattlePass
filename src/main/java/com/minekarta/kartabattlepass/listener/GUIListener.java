package com.minekarta.kartabattlepass.listener;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.gui.*;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import com.minekarta.kartabattlepass.quest.QuestCategory;
import com.minekarta.kartabattlepass.reward.Reward;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GUIListener implements Listener {

    private final KartaBattlePass plugin;

    public GUIListener(KartaBattlePass plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        // Check for custom GUIs using InventoryHolder
        if (event.getInventory().getHolder() instanceof LeaderboardGUI) {
            handleLeaderboardMenuClick(event, player);
            return;
        }
        if (event.getInventory().getHolder() instanceof QuestCategoryGUI) {
            handleQuestCategoryGUIClick(event, player);
            return;
        }
        if (event.getInventory().getHolder() instanceof QuestListGUI) {
            handleQuestListGUIClick(event, player);
            return;
        }

        // Fallback to title-based checks for older GUIs
        String plainTitle = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        String mainMenuTitle = plugin.getConfig().getString("gui.main-menu.title", "KartaBattlePass");
        String rewardsMenuTitlePrefix = plugin.getConfig().getString("gui.rewards.title", "Battle Pass Rewards");

        if (plainTitle.equals(mainMenuTitle)) {
            handleMainMenuClick(event, player);
        } else if (plainTitle.startsWith(rewardsMenuTitlePrefix)) {
            handleRewardsMenuClick(event, player, plainTitle);
        }
    }

    private void handleMainMenuClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        ConfigurationSection itemsConfig = plugin.getConfig().getConfigurationSection("gui.main-menu.items");
        if (itemsConfig == null) return;

        int clickedSlot = event.getSlot();

        if (clickedSlot == itemsConfig.getInt("rewards.slot")) {
            new RewardGUI(plugin, player, 0).open();
        } else if (clickedSlot == itemsConfig.getInt("leaderboards.slot")) {
            new LeaderboardGUI(plugin, player, 0);
        } else if (clickedSlot == itemsConfig.getInt("quests.slot")) {
            new QuestCategoryGUI(plugin, player).open();
        }
    }

    private void handleLeaderboardMenuClick(InventoryClickEvent event, Player player) {
        // The instanceof check in onInventoryClick guarantees this is our GUI.
        // All we need to do is cancel the event to prevent item movement.
        event.setCancelled(true);

        // We only care about clicks inside the GUI, not the player's inventory.
        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        // Handle navigation button clicks
        ConfigurationSection leaderboardConfig = plugin.getConfig().getConfigurationSection("gui.leaderboard");
        if (leaderboardConfig == null) return;

        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        int currentPage = parsePageFromTitle(title) - 1;

        int previousPageSlot = leaderboardConfig.getInt("previous-page.slot", 45);
        int nextPageSlot = leaderboardConfig.getInt("next-page.slot", 53);
        int backButtonSlot = leaderboardConfig.getInt("back-button.slot", 49);

        if (event.getSlot() == previousPageSlot && currentPage > 0) {
            new LeaderboardGUI(plugin, player, currentPage - 1);
        } else if (event.getSlot() == nextPageSlot) {
            new LeaderboardGUI(plugin, player, currentPage + 1);
        } else if (event.getSlot() == backButtonSlot) {
            new MainGUI(plugin).open(player);
        }
        // Any other click (e.g., on a player head) is cancelled and does nothing, which is the desired behavior.
    }

    private void handleQuestCategoryGUIClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        if (event.getClickedInventory() != event.getView().getTopInventory()) return;
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        // Handle back button
        if (event.getSlot() == 49) { // Assuming back button is at slot 49
            new MainGUI(plugin).open(player);
            return;
        }

        // Find the category that was clicked
        QuestCategory clickedCategory = null;
        for (QuestCategory category : plugin.getQuestConfig().getQuestCategories().values()) {
            if (clickedItem.getType() == Material.matchMaterial(category.getDisplayItem())) {
                clickedCategory = category;
                break;
            }
        }

        if (clickedCategory != null) {
            new QuestListGUI(plugin, player, clickedCategory).open();
        }
    }

    private void handleQuestListGUIClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        // Handle back button
        if (event.getSlot() == 49) { // Assuming back button is at slot 49
            new QuestCategoryGUI(plugin, player).open();
        }
    }

    private void handleRewardsMenuClick(InventoryClickEvent event, Player player, String title) {
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        int currentPage = parsePageFromTitle(title) - 1;

        // Handle navigation
        if (event.getSlot() == 45 && clickedItem.getType() == Material.ARROW) { // Previous Page
            new RewardGUI(plugin, player, currentPage - 1).open();
            return;
        }
        if (event.getSlot() == 53 && clickedItem.getType() == Material.ARROW) { // Next Page
            new RewardGUI(plugin, player, currentPage + 1).open();
            return;
        }
        if (event.getSlot() == 49 && clickedItem.getType() == Material.BARRIER) { // Back button
            new MainGUI(plugin).open(player);
            return;
        }

        // Handle reward claiming
        int clickedSlot = event.getSlot();
        boolean isFree = (clickedSlot >= 9 && clickedSlot <= 26);
        boolean isPremium = (clickedSlot >= 36 && clickedSlot <= 53);

        if (isFree || isPremium) {
            Reward reward = getRewardFromSlot(clickedSlot, currentPage, isPremium);
            if (reward == null) return;

            BattlePassPlayer bpp = plugin.getBattlePassStorage().getPlayerData(player.getUniqueId());
            boolean hasPremiumAccess = player.hasPermission("kartabattlepass.premium");
            boolean isClaimed = bpp.hasClaimedReward(reward.getLevel(), reward.getRewardId());
            boolean isUnlocked = bpp.getLevel() >= reward.getLevel();

            if (isUnlocked && !isClaimed && (!reward.isPremium() || hasPremiumAccess)) {
                reward.give(player);
                bpp.addClaimedReward(reward.getLevel(), reward.getRewardId());
                plugin.getBattlePassStorage().savePlayerData(player.getUniqueId(), true);
                // Refresh the GUI
                new RewardGUI(plugin, player, currentPage).open();
            }
        }
    }

    private Reward getRewardFromSlot(int slot, int page, boolean isPremium) {
        List<Reward> allRewards = plugin.getRewardService().getAllRewards().stream()
                .sorted(Comparator.comparingInt(Reward::getLevel))
                .collect(Collectors.toList());

        List<Reward> trackRewards = allRewards.stream()
                .filter(r -> r.isPremium() == isPremium)
                .collect(Collectors.toList());

        int baseSlot = isPremium ? 36 : 9;
        int slotsPerPage = 18;
        int indexOnPage = slot - baseSlot;
        int totalIndex = (page * slotsPerPage) + indexOnPage;

        if (totalIndex >= 0 && totalIndex < trackRewards.size()) {
            return trackRewards.get(totalIndex);
        }
        return null;
    }

    private int parsePageFromTitle(String title) {
        try {
            int lastSpace = title.lastIndexOf(" ");
            if (lastSpace != -1) {
                return Integer.parseInt(title.substring(lastSpace + 1));
            }
        } catch (NumberFormatException e) {
            // Fallback for safety
        }
        return 1;
    }
}
