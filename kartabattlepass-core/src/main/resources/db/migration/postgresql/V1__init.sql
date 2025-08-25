CREATE TABLE IF NOT EXISTS kbp_players (
    uuid UUID PRIMARY KEY,
    name VARCHAR(16) NOT NULL,
    first_seen TIMESTAMPTZ NOT NULL,
    last_seen TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS kbp_seasons (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_ts TIMESTAMPTZ NOT NULL,
    end_ts TIMESTAMPTZ NOT NULL,
    timezone VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS kbp_passes (
    id BIGSERIAL PRIMARY KEY,
    season_id BIGINT NOT NULL REFERENCES kbp_seasons(id) ON DELETE CASCADE,
    player_uuid UUID NOT NULL REFERENCES kbp_players(uuid) ON DELETE CASCADE,
    pass_type VARCHAR(10) NOT NULL, -- FREE, PREMIUM
    purchase_time TIMESTAMPTZ NOT NULL,
    UNIQUE (season_id, player_uuid)
);

CREATE TABLE IF NOT EXISTS kbp_player_progress (
    player_uuid UUID PRIMARY KEY REFERENCES kbp_players(uuid) ON DELETE CASCADE,
    points BIGINT NOT NULL DEFAULT 0,
    tier INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS kbp_quest_progress (
    player_uuid UUID NOT NULL REFERENCES kbp_players(uuid) ON DELETE CASCADE,
    quest_id VARCHAR(255) NOT NULL,
    progress_data JSONB NOT NULL,
    status VARCHAR(20) NOT NULL, -- IN_PROGRESS, COMPLETED, CLAIMED
    updated_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (player_uuid, quest_id)
);

CREATE TABLE IF NOT EXISTS kbp_rewards_claimed (
    player_uuid UUID NOT NULL REFERENCES kbp_players(uuid) ON DELETE CASCADE,
    reward_id VARCHAR(255) NOT NULL, -- Can be tier level (e.g., "tier_1_free") or quest reward ID
    claimed_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (player_uuid, reward_id)
);

CREATE TABLE IF NOT EXISTS kbp_active_boosters (
    id BIGSERIAL PRIMARY KEY,
    booster_def_id VARCHAR(255) NOT NULL, -- The ID from boosters.yml
    player_uuid UUID REFERENCES kbp_players(uuid) ON DELETE CASCADE, -- NULL for global boosters
    activated_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS kbp_leaderboard_snapshots (
    id BIGSERIAL PRIMARY KEY,
    metric VARCHAR(20) NOT NULL, -- POINTS, TIERS
    created_at TIMESTAMPTZ NOT NULL,
    payload JSONB NOT NULL
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_kbp_passes_player ON kbp_passes(player_uuid);
CREATE INDEX IF NOT EXISTS idx_kbp_quest_progress_player ON kbp_quest_progress(player_uuid);
CREATE INDEX IF NOT EXISTS idx_kbp_rewards_claimed_player ON kbp_rewards_claimed(player_uuid);
CREATE INDEX IF NOT EXISTS idx_kbp_active_boosters_player ON kbp_active_boosters(player_uuid);
CREATE INDEX IF NOT EXISTS idx_kbp_active_boosters_expiry ON kbp_active_boosters(expires_at);
