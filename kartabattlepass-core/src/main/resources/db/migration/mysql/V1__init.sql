CREATE TABLE IF NOT EXISTS kbp_players (
    uuid BINARY(16) PRIMARY KEY,
    name VARCHAR(16) NOT NULL,
    first_seen TIMESTAMP NOT NULL,
    last_seen TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS kbp_seasons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_ts TIMESTAMP NOT NULL,
    end_ts TIMESTAMP NOT NULL,
    timezone VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS kbp_passes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    season_id BIGINT NOT NULL,
    player_uuid BINARY(16) NOT NULL,
    pass_type VARCHAR(10) NOT NULL, -- FREE, PREMIUM
    purchase_time TIMESTAMP NOT NULL,
    FOREIGN KEY (season_id) REFERENCES kbp_seasons(id) ON DELETE CASCADE,
    FOREIGN KEY (player_uuid) REFERENCES kbp_players(uuid) ON DELETE CASCADE,
    UNIQUE (season_id, player_uuid)
);

CREATE TABLE IF NOT EXISTS kbp_player_progress (
    player_uuid BINARY(16) PRIMARY KEY,
    points BIGINT NOT NULL DEFAULT 0,
    tier INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (player_uuid) REFERENCES kbp_players(uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS kbp_quest_progress (
    player_uuid BINARY(16) NOT NULL,
    quest_id VARCHAR(255) NOT NULL,
    progress_data JSON NOT NULL,
    status VARCHAR(20) NOT NULL, -- IN_PROGRESS, COMPLETED, CLAIMED
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (player_uuid, quest_id),
    FOREIGN KEY (player_uuid) REFERENCES kbp_players(uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS kbp_rewards_claimed (
    player_uuid BINARY(16) NOT NULL,
    reward_id VARCHAR(255) NOT NULL,
    claimed_at TIMESTAMP NOT NULL,
    PRIMARY KEY (player_uuid, reward_id),
    FOREIGN KEY (player_uuid) REFERENCES kbp_players(uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS kbp_active_boosters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booster_def_id VARCHAR(255) NOT NULL,
    player_uuid BINARY(16), -- NULL for global boosters
    activated_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    FOREIGN KEY (player_uuid) REFERENCES kbp_players(uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS kbp_leaderboard_snapshots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    metric VARCHAR(20) NOT NULL, -- POINTS, TIERS
    created_at TIMESTAMP NOT NULL,
    payload JSON NOT NULL
);

-- Indexes for performance
CREATE INDEX idx_kbp_passes_player ON kbp_passes(player_uuid);
CREATE INDEX idx_kbp_quest_progress_player ON kbp_quest_progress(player_uuid);
CREATE INDEX idx_kbp_rewards_claimed_player ON kbp_rewards_claimed(player_uuid);
CREATE INDEX idx_kbp_active_boosters_player ON kbp_active_boosters(player_uuid);
CREATE INDEX idx_kbp_active_boosters_expiry ON kbp_active_boosters(expires_at);
