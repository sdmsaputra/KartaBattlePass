package com.minekarta.kartabattlepass.reward;

import com.minekarta.kartabattlepass.KartaBattlePass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PermissionReward extends Reward {
    private final String permission;
    private final String duration;

    public PermissionReward(String track, int level, String rewardId, String permission, String duration) {
        super(track, level, rewardId);
        this.permission = permission;
        this.duration = duration;
    }

    @Override
    public void give(Player player) {
        KartaBattlePass plugin = KartaBattlePass.getInstance();
        if (plugin.getVaultHook() != null && plugin.getVaultHook().getPermission() != null) {
            // Note: Vault's API for temporary permissions is not standard.
            // A direct hook into a specific permission plugin (like LuckPerms) is needed for reliable temp perms.
            // This implementation grants permanent permissions as a fallback.
            plugin.getVaultHook().getPermission().playerAdd(null, player, permission);
        } else {
            plugin.getLogger().warning("Could not grant permission '" + permission + "' because Vault or a permissions plugin is not hooked.");
        }
    }

    @Override
    public ItemStack getDisplayItem() {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Permission Reward", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Grants a permission node upon claiming.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(" "));
        lore.add(Component.text("Permission: ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(permission, NamedTextColor.WHITE)));

        if (duration != null && !duration.isEmpty()) {
            lore.add(Component.text("Duration: ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(duration, NamedTextColor.WHITE)));
        } else {
            lore.add(Component.text("Duration: ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("Permanent", NamedTextColor.WHITE)));
        }

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
