package com.karta.battlepass.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.karta.battlepass.api.data.player.PlayerProfile;
import com.karta.battlepass.bukkit.KartaBattlePassPlugin;
import com.karta.battlepass.core.config.StorageConfig;
import com.karta.battlepass.core.db.DatabaseManager;
import com.karta.battlepass.core.db.dao.PlayerDao;
import com.karta.battlepass.core.scheduler.KartaScheduler;
import com.karta.battlepass.core.scheduler.KartaTask;
import com.karta.battlepass.core.service.ServiceRegistry;
import com.karta.battlepass.core.service.impl.PassServiceImpl;
import com.karta.battlepass.core.service.impl.PlayerServiceImpl;
import com.karta.battlepass.core.service.impl.SeasonServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceIntegrationTest {

    private ServerMock server;
    private KartaBattlePassPlugin plugin;
    private PlayerServiceImpl playerService;
    private DatabaseManager databaseManager;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(KartaBattlePassPlugin.class);

        // Setup a real DB for this integration test
        StorageConfig.PoolOptions poolOptions = new StorageConfig.PoolOptions(1, 30, 600, 1800);
        StorageConfig storageConfig = new StorageConfig(StorageConfig.Type.SQLITE, "jdbc:sqlite:" + new File(tempDir, "test.db").getAbsolutePath(), "", "", poolOptions, "main");
        databaseManager = new DatabaseManager(storageConfig);

        ServiceRegistry registry = new ServiceRegistry();
        KartaScheduler scheduler = new TestScheduler();

        playerService = new PlayerServiceImpl(registry, databaseManager.getJdbi(), scheduler);
        registry.register(com.karta.battlepass.api.service.PlayerService.class, playerService);
        registry.register(com.karta.battlepass.api.service.SeasonService.class, new SeasonServiceImpl(new AtomicReference<>(null))); // Dummy
        registry.register(com.karta.battlepass.api.service.PassService.class, new PassServiceImpl(registry, scheduler, databaseManager.getJdbi()));
    }

    @AfterEach
    void tearDown() {
        databaseManager.close();
        MockBukkit.unmock();
    }

    // A simple scheduler that runs tasks immediately on the same thread for testing.
    private static class TestScheduler implements KartaScheduler {
        @Override public void runAsync(@NotNull Runnable task) { task.run(); }
        @Override public <T> CompletableFuture<T> supplyAsync(@NotNull Supplier<T> task) { return CompletableFuture.completedFuture(task.get()); }
        @Override public void runSync(@NotNull Runnable task) { task.run(); }
        @Override public @NotNull KartaTask runLaterAsync(@NotNull Runnable task, @NotNull Duration delay) { return dummyTask(); }
        @Override public @NotNull KartaTask runLaterSync(@NotNull Runnable task, @NotNull Duration delay) { return dummyTask(); }
        @Override public @NotNull KartaTask runTimerAsync(@NotNull Runnable task, @NotNull Duration delay, @NotNull Duration period) { return dummyTask(); }
        @Override public @NotNull KartaTask runTimerSync(@NotNull Runnable task, @NotNull Duration delay, @NotNull Duration period) { return dummyTask(); }
        private KartaTask dummyTask() {
            return new KartaTask() {
                @Override public void cancel() {}
                @Override public boolean isCancelled() { return false; }
            };
        }
    }

    @Test
    void testHandlePlayerJoin() {
        UUID playerId = UUID.randomUUID();
        String playerName = "TestPlayer";

        // First join
        playerService.handlePlayerJoin(playerId, playerName).join();

        PlayerDao playerDao = databaseManager.getJdbi().onDemand(PlayerDao.class);
        assertTrue(playerDao.findById(playerId).isPresent(), "Player should be inserted on first join");

        // Second join with name change
        String newPlayerName = "TestPlayer2";
        playerService.handlePlayerJoin(playerId, newPlayerName).join();

        Optional<PlayerProfile> profile = playerService.getPlayerProfile(playerId).join();
        assertTrue(profile.isPresent());
        assertEquals(newPlayerName, profile.get().name(), "Player name should be updated on subsequent join");
    }
}
