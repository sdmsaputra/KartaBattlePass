package com.minekarta.kartabattlepass.hooks;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getServer;

public class VaultHook {

    private final JavaPlugin plugin;
    private Economy economy = null;
    private Permission permission = null;

    public VaultHook(JavaPlugin plugin) {
        this.plugin = plugin;
        setupEconomy();
        setupPermissions();
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("Vault plugin not found. Economy features will be disabled.");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("No economy plugin found. Economy features will be disabled.");
            return;
        }
        economy = rsp.getProvider();
        plugin.getLogger().info("Successfully hooked into " + economy.getName() + " for economy.");
    }

    private void setupPermissions() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            // Warning already sent by setupEconomy
            return;
        }
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            plugin.getLogger().warning("No permissions plugin found. Permission features will be disabled.");
            return;
        }
        permission = rsp.getProvider();
        plugin.getLogger().info("Successfully hooked into " + permission.getName() + " for permissions.");
    }

    public Economy getEconomy() {
        return economy;
    }

    public Permission getPermission() {
        return permission;
    }
}
