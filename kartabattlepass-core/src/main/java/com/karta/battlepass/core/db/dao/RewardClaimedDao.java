package com.karta.battlepass.core.db.dao;

import com.karta.battlepass.core.db.records.RewardClaimedRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jdbi.v3.sqlobject.config.RegisterRecordMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

/** DAO for the `kbp_rewards_claimed` table. */
@RegisterRecordMapper(RewardClaimedRecord.class)
public interface RewardClaimedDao {

    @SqlUpdate(
            "INSERT INTO kbp_rewards_claimed (player_uuid, reward_id, claimed_at) VALUES (:playerUuid, :rewardId, :claimedAt)")
    void insert(@BindBean RewardClaimedRecord record);

    @SqlQuery("SELECT * FROM kbp_rewards_claimed WHERE player_uuid = :uuid")
    List<RewardClaimedRecord> findAllForPlayer(@Bind("uuid") UUID uuid);

    @SqlQuery(
            "SELECT * FROM kbp_rewards_claimed WHERE player_uuid = :uuid AND reward_id = :rewardId")
    Optional<RewardClaimedRecord> findById(
            @Bind("uuid") UUID uuid, @Bind("rewardId") String rewardId);
}
