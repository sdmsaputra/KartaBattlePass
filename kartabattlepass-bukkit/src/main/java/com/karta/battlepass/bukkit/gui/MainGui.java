package com.karta.battlepass.bukkit.gui;

import com.karta.battlepass.api.service.PlayerService;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MainGui extends Menu {

    private final PlayerService playerService;

    public MainGui(Player player, PlayerService playerService) {
        super(player, 54, Component.text("KartaBattlePass"));
        this.playerService = playerService;
        open();
        initializeItems();
    }

    private void initializeItems() {
        playerService
                .getPlayerProfile(player.getUniqueId())
                .thenAccept(
                        profileOpt -> {
                            if (profileOpt.isEmpty()) {
                                player.sendMessage("Could not load your profile.");
                                return;
                            }
                            var profile = profileOpt.get();

                            inventory.setItem(
                                    13,
                                    createGuiItem(
                                            Material.PLAYER_HEAD,
                                            "<gold>Your Profile",
                                            "<gray>Points: <yellow>" + profile.points(),
                                            "<gray>Tier: <yellow>" + profile.tier()));

                            inventory.setItem(
                                    30,
                                    createGuiItem(
                                            Material.BOOK,
                                            "<green>Quests",
                                            "Click to view your quests."));
                            inventory.setItem(
                                    31,
                                    createGuiItem(
                                            Material.CHEST,
                                            "<aqua>Rewards",
                                            "Click to view your rewards."));
                        });
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        // TODO: Handle clicks to open other menus
        switch (event.getSlot()) {
            case 30:
                player.sendMessage("Quests menu not yet implemented.");
                break;
            case 31:
                player.sendMessage("Rewards menu not yet implemented.");
                break;
        }
    }
}
