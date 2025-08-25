package com.karta.battlepass.bukkit.listener;

import com.karta.battlepass.api.service.PlayerService;
import com.karta.battlepass.core.service.ServiceRegistry;
import com.karta.battlepass.core.service.impl.PlayerServiceImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final ServiceRegistry registry;

    public PlayerListener(ServiceRegistry registry) {
        this.registry = registry;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // We need to access the implementation-specific method, so we cast.
        PlayerService service = registry.playerService();
        if (service instanceof PlayerServiceImpl impl) {
            impl.handlePlayerJoin(player.getUniqueId(), player.getName());
        }
    }
}
