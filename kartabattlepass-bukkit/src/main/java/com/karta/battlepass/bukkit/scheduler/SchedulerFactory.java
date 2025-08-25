package com.karta.battlepass.bukkit.scheduler;

import com.karta.battlepass.core.scheduler.KartaScheduler;
import org.bukkit.plugin.Plugin;

public class SchedulerFactory {

    private static final boolean FOLIA_DETECTED;

    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            // Not a Folia server
        }
        FOLIA_DETECTED = folia;
    }

    public static KartaScheduler create(Plugin plugin) {
        if (FOLIA_DETECTED) {
            plugin.getLogger().info("Folia detected. Using Folia-compatible scheduler.");
            return new FoliaScheduler(plugin);
        } else {
            plugin.getLogger().info("Using standard Bukkit scheduler.");
            return new BukkitScheduler(plugin);
        }
    }
}
