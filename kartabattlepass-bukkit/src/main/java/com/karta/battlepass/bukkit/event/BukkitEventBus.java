package com.karta.battlepass.bukkit.event;

import com.karta.battlepass.api.event.KBPTierChangeEvent;
import com.karta.battlepass.core.event.bus.EventBus;
import com.karta.battlepass.core.event.data.KartaEvent;
import com.karta.battlepass.core.event.data.TierChangeEventData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class BukkitEventBus implements EventBus {

    private final Plugin plugin;

    public BukkitEventBus(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void fire(@NotNull KartaEvent event) {
        // This could be more sophisticated with a map of handlers, but for now a switch is fine.
        if (event instanceof TierChangeEventData data) {
            Player player = Bukkit.getPlayer(data.playerUuid());
            if (player != null) {
                KBPTierChangeEvent bukkitEvent =
                        new KBPTierChangeEvent(player, data.oldTier(), data.newTier());
                plugin.getServer().getPluginManager().callEvent(bukkitEvent);
            }
        }
        // TODO: Add cases for other events
    }
}
