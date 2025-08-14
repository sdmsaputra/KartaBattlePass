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
    private final Material material;
    private final int amount;
    private final String name;
    private final List<String> lore;
    private final List<String> enchantments;

    public ItemReward(String track, Material material, int amount, String name, List<String> lore, List<String> enchantments) {
        super(track);
        this.material = material;
        this.amount = amount;
        this.name = name;
        this.lore = lore;
        this.enchantments = enchantments;
    }

    @Override
    public void give(Player player) {
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
                        int level = Integer.parseInt(parts[1]);
                        meta.addEnchant(enchantment, level, true);
                    }
                }
            }
        }

        item.setItemMeta(meta);
        player.getInventory().addItem(item);
    }
}
