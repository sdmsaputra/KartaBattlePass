package com.karta.battlepass.core.db.dao;

import com.karta.battlepass.core.db.records.PassRecord;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.jdbi.v3.sqlobject.config.RegisterRecordMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

/** DAO for the `kbp_passes` table. */
@RegisterRecordMapper(PassRecord.class)
public interface PassDao {

    @SqlQuery("SELECT * FROM kbp_passes WHERE player_uuid = :uuid AND season_id = :seasonId")
    Optional<PassRecord> findByPlayerAndSeason(
            @Bind("uuid") UUID uuid, @Bind("seasonId") long seasonId);

    @SqlUpdate(
            "INSERT INTO kbp_passes (season_id, player_uuid, pass_type, purchase_time) "
                    + "VALUES (:seasonId, :uuid, :type, :now)")
    @GetGeneratedKeys
    long insert(
            @Bind("seasonId") long seasonId,
            @Bind("uuid") UUID uuid,
            @Bind("type") String type,
            @Bind("now") Instant now);
}
