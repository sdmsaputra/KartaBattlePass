package com.minekarta.kartabattlepass;

import com.minekarta.kartabattlepass.command.KBPCommand;
import com.minekarta.kartabattlepass.expansion.BattlePassExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class KartaBattlePass extends JavaPlugin {

    private static KartaBattlePass instance;
    private MiniMessage miniMessage;

    @Override
    public void onEnable() {
        instance = this;
        this.miniMessage = MiniMessage.miniMessage();

        getLogger().info("Loading configurations...");
        saveDefaultConfig();
        saveDefaultResource("messages.yml");

        getLogger().info("Registering command...");
        getCommand("kbp").setExecutor(new KBPCommand(this));

        getLogger().info("Checking dependencies...");
        checkDependencies();

        getLogger().info("KartaBattlePass v" + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("KartaBattlePass has been disabled.");
    }

    private void checkDependencies() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new BattlePassExpansion(this).register();
            getLogger().info("Successfully hooked into PlaceholderAPI.");
        } else {
            getLogger().warning("PlaceholderAPI not found, placeholders will not work. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return; // Stop further execution
        }

        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            getLogger().info("Successfully hooked into Vault.");
            // new VaultHook(); // Initialize hook
        } else {
            getLogger().warning("Vault not found, some economy features might be disabled.");
        }

        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            getLogger().info("Successfully hooked into Citizens.");
            // new CitizensHook(); // Initialize hook
        } else {
            getLogger().warning("Citizens not found, NPC features will not work.");
        }

        if (Bukkit.getPluginManager().getPlugin("FancyNPC") != null) {
            getLogger().info("Successfully hooked into FancyNPC.");
            // new FancyNPCHook(); // Initialize hook
        } else {
            getLogger().warning("FancyNPC not found, fancy NPC features will not work.");
        }
    }

    private void saveDefaultResource(String resourcePath) {
        File resourceFile = new File(getDataFolder(), resourcePath);
        if (!resourceFile.exists()) {
            saveResource(resourcePath, false);
        }
    }

    public static KartaBattlePass getInstance() {
        return instance;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
}
