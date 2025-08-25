package com.karta.battlepass.core.db.dao;

import com.karta.battlepass.core.db.records.QuestProgressRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jdbi.v3.sqlobject.config.RegisterRecordMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

/** DAO for the `kbp_quest_progress` table. */
@RegisterRecordMapper(QuestProgressRecord.class)
public interface QuestProgressDao {

    @SqlQuery("SELECT * FROM kbp_quest_progress WHERE player_uuid = :uuid AND quest_id = :questId")
    Optional<QuestProgressRecord> findById(
            @Bind("uuid") UUID uuid, @Bind("questId") String questId);

    @SqlQuery("SELECT * FROM kbp_quest_progress WHERE player_uuid = :uuid")
    List<QuestProgressRecord> findAllForPlayer(@Bind("uuid") UUID uuid);

    @SqlUpdate(
            "INSERT INTO kbp_quest_progress (player_uuid, quest_id, progress_data, status, updated_at) "
                    + "VALUES (:playerUuid, :questId, :progressData, :status, :updatedAt)")
    void insert(@BindBean QuestProgressRecord record);

    @SqlUpdate(
            "UPDATE kbp_quest_progress SET progress_data = :progressData, status = :status, updated_at = :updatedAt "
                    + "WHERE player_uuid = :playerUuid AND quest_id = :questId")
    void update(@BindBean QuestProgressRecord record);

    @SqlUpdate("DELETE FROM kbp_quest_progress WHERE player_uuid = :uuid AND quest_id = :questId")
    void delete(@Bind("uuid") UUID uuid, @Bind("questId") String questId);
}
