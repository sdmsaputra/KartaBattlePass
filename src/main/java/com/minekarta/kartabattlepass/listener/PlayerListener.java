package com.minekarta.kartabattlepass.listener;

import com.minekarta.kartabattlepass.storage.BattlePassStorage;
import org.bukkit.entity.Player;
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
        Player player = event.getPlayer();
        storage.loadPlayerData(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        storage.unloadPlayerData(player);
    }
}
