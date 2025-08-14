package com.minekarta.kartabattlepass.listener;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.gui.LeaderboardGUI;
import com.minekarta.kartabattlepass.gui.MainGUI;
import com.minekarta.kartabattlepass.gui.RewardGUI;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
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
        String plainTitle = PlainTextComponentSerializer.plainText().serialize(event.getView().title());

        String mainMenuTitle = plugin.getConfig().getString("gui.main-menu.title", "KartaBattlePass");
        String rewardsMenuTitlePrefix = plugin.getConfig().getString("gui.rewards.title", "Battle Pass Rewards");
        String leaderboardMenuTitlePrefix = "Leaderboard"; // This is hardcoded in LeaderboardGUI for now

        if (plainTitle.equals(mainMenuTitle)) {
            handleMainMenuClick(event, player);
        } else if (plainTitle.startsWith(rewardsMenuTitlePrefix)) {
            handleRewardsMenuClick(event, player, plainTitle);
        } else if (plainTitle.startsWith(leaderboardMenuTitlePrefix)) {
            handleLeaderboardMenuClick(event, player, plainTitle);
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
            plugin.getLeaderboardService().updateLeaderboard(); // Update leaderboard before opening
            new LeaderboardGUI(plugin, player, 0).open();
        } else if (clickedSlot == itemsConfig.getInt("quests.slot")) {
            player.sendMessage("This feature is not yet implemented.");
            player.closeInventory();
        }
    }

    private void handleLeaderboardMenuClick(InventoryClickEvent event, Player player, String title) {
        event.setCancelled(true);
        int currentPage = parsePageFromTitle(title) - 1;

        if (event.getSlot() == 45) { // Previous Page
            new LeaderboardGUI(plugin, player, currentPage - 1).open();
        } else if (event.getSlot() == 53) { // Next Page
            new LeaderboardGUI(plugin, player, currentPage + 1).open();
        } else if (event.getSlot() == 49) { // Back button
            new MainGUI(plugin).open(player);
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
