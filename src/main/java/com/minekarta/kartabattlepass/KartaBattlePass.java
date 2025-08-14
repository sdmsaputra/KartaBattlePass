package com.minekarta.kartabattlepass;

import com.minekarta.kartabattlepass.command.KBPCommand;
import com.minekarta.kartabattlepass.command.KBPCommand;
import com.minekarta.kartabattlepass.config.RewardConfig;
import com.minekarta.kartabattlepass.expansion.BattlePassExpansion;
import com.minekarta.kartabattlepass.hooks.VaultHook;
import com.minekarta.kartabattlepass.listener.GUIListener;
import com.minekarta.kartabattlepass.listener.PlayerListener;
import com.minekarta.kartabattlepass.service.ExperienceService;
import com.minekarta.kartabattlepass.service.RewardService;
import com.minekarta.kartabattlepass.storage.BattlePassStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public final class KartaBattlePass extends JavaPlugin {

    private static KartaBattlePass instance;
    private MiniMessage miniMessage;
    private BattlePassStorage battlePassStorage;
    private RewardService rewardService;
    private ExperienceService experienceService;
    private VaultHook vaultHook;
    private RewardConfig rewardConfig;

    @Override
    public void onEnable() {
        instance = this;
        this.miniMessage = MiniMessage.miniMessage();

        getLogger().info("Loading configurations...");
        saveDefaultConfig();
        saveDefaultResource("messages.yml");
        this.rewardConfig = new RewardConfig(this);

        getLogger().info("Initializing storage...");
        this.battlePassStorage = new BattlePassStorage(this);
        this.rewardService = new RewardService(this);
        this.experienceService = new ExperienceService(this);

        getLogger().info("Registering command...");
        getCommand("kbattlepass").setExecutor(new KBPCommand(this));

        getLogger().info("Registering listeners...");
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);

        getLogger().info("Checking dependencies...");
        checkDependencies();

        // Load data for any players already online (e.g., after a /reload)
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.battlePassStorage.loadPlayerData(player);
        }

        startAutoSaveTask();

        getLogger().info("KartaBattlePass v" + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Saving all player data...");
        if (this.battlePassStorage != null) {
            this.battlePassStorage.saveAllPlayerData();
        }
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
            this.vaultHook = new VaultHook(this);
        } else {
            getLogger().warning("Vault not found, economy and permission features will be disabled.");
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

    private void startAutoSaveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                getLogger().info("Auto-saving player data...");
                battlePassStorage.saveAllPlayerData();
            }
        }.runTaskTimer(this, 20L * 60 * 5, 20L * 60 * 5); // Every 5 minutes
    }

    public static KartaBattlePass getInstance() {
        return instance;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public BattlePassStorage getBattlePassStorage() {
        return battlePassStorage;
    }

    public RewardService getRewardService() {
        return rewardService;
    }

    public ExperienceService getExperienceService() {
        return experienceService;
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }

    public RewardConfig getRewardConfig() {
        return rewardConfig;
    }

    /**
     * Reloads the plugin's configuration and re-initializes services.
     */
    public void reload() {
        getLogger().info("Reloading configurations...");
        reloadConfig();
        saveDefaultConfig(); // Ensure config exists
        this.rewardConfig.reloadConfig();

        // Re-initialize services that depend on the config
        this.battlePassStorage.loadConfigValues();
        this.experienceService = new ExperienceService(this);
        this.rewardService = new RewardService(this);
        getLogger().info("Services re-initialized.");
    }

    /**
     * Adds a specified amount of XP to a player.
     * This will handle level-ups and data saving automatically.
     *
     * @param player The player to give XP to.
     * @param amount The amount of XP to give.
     */
    // This method will be moved to the Experience Service
    // public void addXP(Player player, int amount) {
    //    this.battlePassStorage.addXP(player, amount);
    // }
}
