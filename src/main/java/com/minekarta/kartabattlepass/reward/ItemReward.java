package com.minekarta.kartabattlepass.reward;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class ItemReward extends Reward {
    private final ItemStack displayItem;

    public ItemReward(String track, int level, String rewardId, Material material, int amount, String name, List<String> lore, List<String> enchantments) {
        super(track, level, rewardId);
        this.displayItem = createDisplayItem(material, amount, name, lore, enchantments);
    }

    private ItemStack createDisplayItem(Material material, int amount, String name, List<String> lore, List<String> enchantments) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (name != null && !name.isEmpty()) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }

        if (lore != null && !lore.isEmpty()) {
            meta.setLore(lore.stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList()));
        }

        if (enchantments != null && !enchantments.isEmpty()) {
            for (String ench : enchantments) {
                String[] parts = ench.split(":");
                if (parts.length == 2) {
                    Enchantment enchantment = Enchantment.getByName(parts[0].toUpperCase());
                    if (enchantment != null) {
                        try {
                            int enchLevel = Integer.parseInt(parts[1]);
                            meta.addEnchant(enchantment, enchLevel, true);
                        } catch (NumberFormatException e) {
                            // Ignore invalid enchantment level
                        }
                    }
                }
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void give(Player player) {
        player.getInventory().addItem(this.displayItem.clone());
    }

    @Override
    public ItemStack getDisplayItem() {
        return this.displayItem.clone();
    }
}
