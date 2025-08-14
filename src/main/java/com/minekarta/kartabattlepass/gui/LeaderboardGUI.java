package com.minekarta.kartabattlepass.gui;

import com.minekarta.kartabattlepass.KartaBattlePass;
import com.minekarta.kartabattlepass.model.BattlePassPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LeaderboardGUI {
    private final KartaBattlePass plugin;
    private final MiniMessage miniMessage;
    private final Player viewer;
    private final int page;
    private Inventory inventory;

    public LeaderboardGUI(KartaBattlePass plugin, Player viewer, int page) {
        this.plugin = plugin;
        this.miniMessage = plugin.getMiniMessage();
        this.viewer = viewer;
        this.page = page;

        loadAndDisplayLeaderboard();
    }

    private void loadAndDisplayLeaderboard() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            List<BattlePassPlayer> allPlayers = plugin.getBattlePassStorage().getAllPlayerData();
            allPlayers.sort(Comparator.comparingInt(BattlePassPlayer::getLevel).reversed()
                    .thenComparingInt(BattlePassPlayer::getExp).reversed());

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                createInventory(allPlayers);
                open();
            });
        });
    }

    private void createInventory(List<BattlePassPlayer> players) {
        ConfigurationSection guiConfig = plugin.getConfig().getConfigurationSection("gui.leaderboard");
        if (guiConfig == null) {
            viewer.sendMessage(Component.text("Leaderboard GUI not configured!"));
            return;
        }

        String titleTemplate = guiConfig.getString("title", "<gold>Leaderboard - Page <page>");
        int size = guiConfig.getInt("size", 54);
        int playersPerPage = 45;

        int totalPages = Math.max(1, (int) Math.ceil((double) players.size() / playersPerPage));
        int effectivePage = Math.max(0, Math.min(page, totalPages - 1));

        String title = titleTemplate.replace("<page>", String.valueOf(effectivePage + 1));
        this.inventory = Bukkit.createInventory(null, size, miniMessage.deserialize(title));

        initializeItems(guiConfig, players, effectivePage, totalPages);
    }

    private void initializeItems(ConfigurationSection guiConfig, List<BattlePassPlayer> players, int effectivePage, int totalPages) {
        for (int i = 0; i < 45; i++) {
            int playerIndex = effectivePage * 45 + i;
            if (playerIndex < players.size()) {
                BattlePassPlayer bpp = players.get(playerIndex);
                inventory.setItem(i, createPlayerHead(bpp, playerIndex + 1));
            } else {
                inventory.setItem(i, new ItemStack(Material.AIR));
            }
        }

        if (effectivePage > 0) {
            inventory.setItem(45, createNavItem("previous-page", guiConfig));
        }

        if (effectivePage < totalPages - 1) {
            inventory.setItem(53, createNavItem("next-page", guiConfig));
        }

        inventory.setItem(49, createNavItem("back-button", guiConfig));
    }

    private ItemStack createPlayerHead(BattlePassPlayer bpp, int rank) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(bpp.getUuid());
        meta.setOwningPlayer(offlinePlayer);

        String nameTemplate = "<gold>#<rank> <player_name>";
        meta.displayName(miniMessage.deserialize(nameTemplate,
                Placeholder.component("rank", Component.text(rank)),
                Placeholder.component("player_name", Component.text(bpp.getName()))));

        List<String> loreTemplate = new ArrayList<>();
        loreTemplate.add("<gray>Level: <white><level>");
        loreTemplate.add("<gray>EXP: <white><exp>");
        meta.lore(loreTemplate.stream()
                .map(line -> miniMessage.deserialize(line,
                        Placeholder.component("level", Component.text(bpp.getLevel())),
                        Placeholder.component("exp", Component.text(bpp.getExp()))))
                .collect(Collectors.toList()));

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createNavItem(String key, ConfigurationSection guiConfig) {
        ConfigurationSection itemConfig = guiConfig.getConfigurationSection(key);
        if (itemConfig == null) return new ItemStack(Material.AIR);

        Material material = Material.matchMaterial(itemConfig.getString("material", "STONE"));
        String name = itemConfig.getString("name", " ");
        List<String> lore = itemConfig.getStringList("lore");

        ItemStack item = new ItemStack(material != null ? material : Material.STONE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(miniMessage.deserialize(name));
        meta.lore(lore.stream().map(miniMessage::deserialize).collect(Collectors.toList()));
        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        if (inventory != null) {
            viewer.openInventory(inventory);
        }
    }
}
