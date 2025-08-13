package com.minekarta.kartabattlepass.listener;

import com.minekarta.kartabattlepass.storage.BattlePassStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final BattlePassStorage storage;

    public PlayerListener(BattlePassStorage storage) {
        this.storage = storage;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Load player data into cache when they join
        storage.loadPlayerData(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Save player data from cache to file when they quit
        storage.savePlayerData(event.getPlayer(), true);
    }
}
