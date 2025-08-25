package com.karta.battlepass.tests;

import com.karta.battlepass.core.config.StorageConfig;
import com.karta.battlepass.core.db.DatabaseManager;
import com.karta.battlepass.core.db.dao.PlayerDao;
import com.karta.battlepass.core.db.records.PlayerRecord;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseIntegrationTest {

    private DatabaseManager databaseManager;
    private Jdbi jdbi;
    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        StorageConfig.PoolOptions poolOptions = new StorageConfig.PoolOptions(1, 30, 600, 1800);
        StorageConfig storageConfig = new StorageConfig(
                StorageConfig.Type.SQLITE,
                "jdbc:sqlite:" + new File(tempDir, "test.db").getAbsolutePath(),
                "", "", poolOptions, "main"
        );
        databaseManager = new DatabaseManager(storageConfig);
        jdbi = databaseManager.getJdbi();
    }

    @AfterEach
    void tearDown() {
        databaseManager.close();
    }

    @Test
    void testPlayerDaoUpsert() {
        PlayerDao dao = jdbi.onDemand(PlayerDao.class);

        UUID playerId = UUID.randomUUID();
        String playerName = "TestPlayer";
        Instant firstSeen = Instant.now();

        // Test insert
        dao.insert(playerId, playerName, firstSeen);
        Optional<PlayerRecord> playerOpt = dao.findById(playerId);
        assertTrue(playerOpt.isPresent());
        assertEquals(playerName, playerOpt.get().name());

        // Test update
        String newPlayerName = "TestPlayerNew";
        Instant lastSeen = Instant.now().plusSeconds(60);
        dao.update(playerId, newPlayerName, lastSeen);
        playerOpt = dao.findById(playerId);
        assertTrue(playerOpt.isPresent());
        assertEquals(newPlayerName, playerOpt.get().name());
    }
}
