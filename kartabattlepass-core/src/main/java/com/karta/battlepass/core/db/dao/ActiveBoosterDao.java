package com.karta.battlepass.core.db.dao;

import com.karta.battlepass.core.db.records.ActiveBoosterRecord;
import org.jdbi.v3.sqlobject.config.RegisterRecordMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DAO for the `kbp_active_boosters` table.
 */
@RegisterRecordMapper(ActiveBoosterRecord.class)
public interface ActiveBoosterDao {

    @SqlUpdate("INSERT INTO kbp_active_boosters (booster_def_id, player_uuid, activated_at, expires_at) " +
               "VALUES (:boosterDefId, :playerUuid, :activatedAt, :expiresAt)")
    @GetGeneratedKeys
    long insert(@BindBean ActiveBoosterRecord record);

    @SqlQuery("SELECT * FROM kbp_active_boosters WHERE expires_at > :now AND (player_uuid = :uuid OR player_uuid IS NULL)")
    List<ActiveBoosterRecord> findActiveForPlayer(@Bind("uuid") UUID uuid, @Bind("now") Instant now);

    @SqlUpdate("DELETE FROM kbp_active_boosters WHERE id = :id")
    void deleteById(@Bind("id") long id);
}
