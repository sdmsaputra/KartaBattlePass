package com.karta.battlepass.bukkit;

import com.karta.battlepass.api.KartaBattlePassAPI;
import com.karta.battlepass.api.service.BoosterService;
import com.karta.battlepass.api.service.LeaderboardService;
import com.karta.battlepass.api.service.PassService;
import com.karta.battlepass.api.service.PlayerService;
import com.karta.battlepass.api.service.QuestService;
import com.karta.battlepass.api.service.RewardService;
import com.karta.battlepass.api.service.SeasonService;
import com.karta.battlepass.bukkit.command.KbpCommand;
import com.karta.battlepass.bukkit.gui.GuiListener;
import com.karta.battlepass.bukkit.integration.economy.VaultEconomyProvider;
import com.karta.battlepass.bukkit.integration.papi.KartaBattlePassExpansion;
import com.karta.battlepass.bukkit.listener.MasterQuestListener;
import com.karta.battlepass.bukkit.event.BukkitEventBus;
import com.karta.battlepass.bukkit.listener.PlayerListener;
import com.karta.battlepass.bukkit.scheduler.SchedulerFactory;
import com.karta.battlepass.bukkit.quest.PlaytimeTracker;
import com.karta.battlepass.core.config.ConfigManager;
import com.karta.battlepass.core.config.MainConfig;
import com.karta.battlepass.core.db.DatabaseManager;
import com.karta.battlepass.core.economy.EconomyService;
import com.karta.battlepass.core.economy.EconomyServiceImpl;
import com.karta.battlepass.core.event.bus.EventBus;
import com.karta.battlepass.core.scheduler.KartaScheduler;
import com.karta.battlepass.core.service.ServiceRegistry;
import com.karta.battlepass.core.service.impl.BoosterServiceImpl;
import com.karta.battlepass.core.service.impl.LeaderboardServiceImpl;
import com.karta.battlepass.core.service.impl.PassServiceImpl;
import com.karta.battlepass.core.service.impl.PlayerServiceImpl;
import com.karta.battlepass.core.service.impl.QuestServiceImpl;
import com.karta.battlepass.core.service.impl.RewardServiceImpl;
import com.karta.battlepass.core.service.impl.SeasonServiceImpl;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class KartaBattlePassPlugin extends JavaPlugin {

    private DatabaseManager databaseManager;
    private ServiceRegistry serviceRegistry;
    private KartaScheduler scheduler;
    private MainConfig mainConfig;
    private KartaBattlePassAPI api;

    @Override
    public void onEnable() {
        try {
            // 1. Initialize Config
            ConfigManager configManager = new ConfigManager(getDataFolder(), getClassLoader());
            this.mainConfig = configManager.loadConfig("config.yml", MainConfig.class);
            AtomicReference<MainConfig> configRef = new AtomicReference<>(this.mainConfig);

            // 2. Initialize Scheduler & EventBus
            this.scheduler = SchedulerFactory.create(this);
            EventBus eventBus = new BukkitEventBus(this);

            // 3. Initialize Database
            this.databaseManager = new DatabaseManager(mainConfig.storage());

            // 4. Initialize Services
            this.serviceRegistry = new ServiceRegistry();
            serviceRegistry.register(PlayerService.class, new PlayerServiceImpl(serviceRegistry, databaseManager.getJdbi(), scheduler, eventBus));
            serviceRegistry.register(SeasonService.class, new SeasonServiceImpl(configRef));
            serviceRegistry.register(PassService.class, new PassServiceImpl(serviceRegistry, scheduler, databaseManager.getJdbi()));
            serviceRegistry.register(QuestService.class, new QuestServiceImpl(serviceRegistry, configManager));
            serviceRegistry.register(RewardService.class, new RewardServiceImpl(serviceRegistry, configManager, scheduler, databaseManager.getJdbi()));
            serviceRegistry.register(BoosterService.class, new BoosterServiceImpl(serviceRegistry));
            serviceRegistry.register(LeaderboardService.class, new LeaderboardServiceImpl(serviceRegistry));

            // 5. Register API
            this.api = new KartaBattlePassAPIImpl(this.serviceRegistry, this);
            getServer().getServicesManager().register(KartaBattlePassAPI.class, this.api, this, ServicePriority.Normal);

            // 6. Register Commands & Listeners
            getCommand("kartabattlepass").setExecutor(new KbpCommand(this.serviceRegistry));
            getServer().getPluginManager().registerEvents(new PlayerListener(this.serviceRegistry), this);
            getServer().getPluginManager().registerEvents(new GuiListener(), this);
            new MasterQuestListener(this.api.getQuestService(), this);

            // 7. Initialize Trackers & Integrations
            new PlaytimeTracker(this.api.getQuestService(), this.scheduler).start();
            setupEconomy();
            setupIntegrations();

            getLogger().info("KartaBattlePass enabled successfully!");

        } catch (IOException e) {
            getLogger().severe("Failed to load configuration files. Disabling plugin.");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        } catch (Exception e) {
            getLogger().severe("An unexpected error occurred during startup. Disabling plugin.");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (this.databaseManager != null) {
            this.databaseManager.close();
        }
        getLogger().info("KartaBattlePass disabled.");
    }

    public KartaBattlePassAPI getApi() {
        return api;
    }

    private void setupIntegrations() {
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new KartaBattlePassExpansion(this.api).register();
            getLogger().info("Registered PlaceholderAPI expansion.");
        }
    }

    private void setupEconomy() {
        EconomyService economyService = new EconomyServiceImpl();
        serviceRegistry.register(EconomyService.class, economyService);
        // TODO: Register other economy providers like KartaEmeraldCurrency

        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            VaultEconomyProvider vaultProvider = new VaultEconomyProvider();
            if (vaultProvider.setup()) {
                economyService.registerProvider(vaultProvider);
                getLogger().info("Registered Vault economy provider.");
            }
        }

        if (economyService.setActiveProvider(mainConfig.economy().provider())) {
            getLogger().info("Active economy provider set to: " + mainConfig.economy().provider());
        } else {
            getLogger().warning("Could not set active economy provider to " + mainConfig.economy().provider() + ". No economy features will be available.");
        }
    }
}
