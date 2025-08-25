package com.karta.battlepass.core.db.dao;

import com.karta.battlepass.core.db.records.PlayerRecord;
import org.jdbi.v3.sqlobject.config.RegisterRecordMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * DAO for the `kbp_players` table.
 */
@RegisterRecordMapper(PlayerRecord.class)
public interface PlayerDao {

    @SqlQuery("SELECT * FROM kbp_players WHERE uuid = :uuid")
    Optional<PlayerRecord> findById(@Bind("uuid") UUID uuid);

    @SqlUpdate("INSERT INTO kbp_players (uuid, name, first_seen, last_seen) VALUES (:uuid, :name, :now, :now)")
    void insert(@Bind("uuid") UUID uuid, @Bind("name") String name, @Bind("now") Instant now);

    @SqlUpdate("UPDATE kbp_players SET name = :name, last_seen = :now WHERE uuid = :uuid")
    void update(@Bind("uuid") UUID uuid, @Bind("name") String name, @Bind("now") Instant now);
}
