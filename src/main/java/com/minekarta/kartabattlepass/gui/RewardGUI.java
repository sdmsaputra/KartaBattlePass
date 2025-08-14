package com.minekarta.kartabattlepass.gui;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import com.minekarta.kartabattlepass.reward.Reward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RewardGUI {

    private final KartaBattlePass plugin;
    private final Player player;
    private final BattlePassPlayer bpp;

    public RewardGUI(KartaBattlePass plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.bpp = plugin.getBattlePassStorage().getBattlePassPlayer(player.getUniqueId());
    }

    public void open() {
        if (bpp == null) {
            player.sendMessage(ChatColor.RED + "Could not open reward GUI, your data is not loaded.");
            return;
        }

        Inventory gui = Bukkit.createInventory(null, 54, "Battle Pass Rewards");

        // Example: Display rewards for levels 1-54
        for (int level = 1; level <= 54; level++) {
            List<Reward> rewards = plugin.getRewardService().getRewardsForLevel(level);
            ItemStack displayItem;

            if (bpp.getLevel() >= level) {
                // Player has reached this level
                boolean allClaimed = areAllRewardsClaimed(level, rewards);
                if (allClaimed) {
                    displayItem = createDisplayItem(Material.CHEST,
                            "&aLevel " + level + " (Claimed)",
                            Collections.singletonList("&7You have claimed all rewards for this level."));
                } else {
                    displayItem = createDisplayItem(Material.ENDER_CHEST,
                            "&eLevel " + level + " (Click to Claim)",
                            generateRewardLore(rewards, level));
                }
            } else {
                // Level not yet reached
                displayItem = createDisplayItem(Material.BARRIER,
                        "&cLevel " + level + " (Locked)",
                        generateRewardLore(rewards, level));
            }
            gui.setItem(level - 1, displayItem);
        }

        player.openInventory(gui);
    }

    private boolean areAllRewardsClaimed(int level, List<Reward> rewards) {
        int rewardIndex = 0;
        for (Reward reward : rewards) {
            String rewardId = level + ":" + rewardIndex;
            boolean isPremium = reward.isPremium();
            boolean hasPremiumAccess = player.hasPermission("kartabattlepass.premium");

            if (isPremium && !hasPremiumAccess) {
                // Cannot claim premium, so don't count it as 'unclaimed' for this check
                rewardIndex++;
                continue;
            }

            if (!bpp.hasClaimedReward(level, rewardId)) {
                return false; // Found at least one unclaimed reward
            }
            rewardIndex++;
        }
        return true;
    }

    private List<String> generateRewardLore(List<Reward> rewards, int level) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        int rewardIndex = 0;
        for (Reward reward : rewards) {
            String rewardId = level + ":" + rewardIndex;
            boolean isClaimed = bpp.hasClaimedReward(level, rewardId);
            String prefix = isClaimed ? "&a✔ " : "&c✖ ";
            lore.add(prefix + "&7Reward " + (rewardIndex + 1) + (reward.isPremium() ? " &6[Premium]" : ""));
            rewardIndex++;
        }
        return lore;
    }

    private ItemStack createDisplayItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> coloredLore = new ArrayList<>();
        for (String line : lore) {
            coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(coloredLore);

        item.setItemMeta(meta);
        return item;
    }
}
