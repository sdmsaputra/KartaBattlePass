package com.karta.battlepass.core.db.dao;

import com.karta.battlepass.core.db.records.PlayerProgressRecord;
import org.jdbi.v3.sqlobject.config.RegisterRecordMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * DAO for the `kbp_player_progress` table.
 */
@RegisterRecordMapper(PlayerProgressRecord.class)
public interface PlayerProgressDao {

    @SqlQuery("SELECT * FROM kbp_player_progress WHERE player_uuid = :uuid")
    Optional<PlayerProgressRecord> findById(@Bind("uuid") UUID uuid);

    @SqlUpdate("INSERT INTO kbp_player_progress (player_uuid, points, tier, updated_at) " +
               "VALUES (:uuid, :points, :tier, :now)")
    void insert(@Bind("uuid") UUID uuid, @Bind("points") long points, @Bind("tier") int tier, @Bind("now") Instant now);

    @SqlUpdate("UPDATE kbp_player_progress SET points = :points, tier = :tier, updated_at = :now " +
               "WHERE player_uuid = :uuid")
    void update(@Bind("uuid") UUID uuid, @Bind("points") long points, @Bind("tier") int tier, @Bind("now") Instant now);

    @SqlUpdate("UPDATE kbp_player_progress SET points = points + :amount, updated_at = :now " +
               "WHERE player_uuid = :uuid")
    void addPoints(@Bind("uuid") UUID uuid, @Bind("amount") long amount, @Bind("now") Instant now);
}
