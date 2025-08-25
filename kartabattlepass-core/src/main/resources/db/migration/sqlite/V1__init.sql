CREATE TABLE IF NOT EXISTS kbp_players (
    uuid TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    first_seen INTEGER NOT NULL,
    last_seen INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS kbp_seasons (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    start_ts INTEGER NOT NULL,
    end_ts INTEGER NOT NULL,
    timezone TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS kbp_passes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    season_id INTEGER NOT NULL,
    player_uuid TEXT NOT NULL,
    pass_type TEXT NOT NULL, -- FREE, PREMIUM
    purchase_time INTEGER NOT NULL,
    FOREIGN KEY (season_id) REFERENCES kbp_seasons(id) ON DELETE CASCADE,
    FOREIGN KEY (player_uuid) REFERENCES kbp_players(uuid) ON DELETE CASCADE,
    UNIQUE (season_id, player_uuid)
);

CREATE TABLE IF NOT EXISTS kbp_player_progress (
    player_uuid TEXT PRIMARY KEY,
    points INTEGER NOT NULL DEFAULT 0,
    tier INTEGER NOT NULL DEFAULT 0,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY (player_uuid) REFERENCES kbp_players(uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS kbp_quest_progress (
    player_uuid TEXT NOT NULL,
    quest_id TEXT NOT NULL,
    progress_data TEXT NOT NULL, -- JSON stored as TEXT
    status TEXT NOT NULL, -- IN_PROGRESS, COMPLETED, CLAIMED
    updated_at INTEGER NOT NULL,
    PRIMARY KEY (player_uuid, quest_id),
    FOREIGN KEY (player_uuid) REFERENCES kbp_players(uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS kbp_rewards_claimed (
    player_uuid TEXT NOT NULL,
    reward_id TEXT NOT NULL,
    claimed_at INTEGER NOT NULL,
    PRIMARY KEY (player_uuid, reward_id),
    FOREIGN KEY (player_uuid) REFERENCES kbp_players(uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS kbp_active_boosters (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    booster_def_id TEXT NOT NULL,
    player_uuid TEXT, -- NULL for global boosters
    activated_at INTEGER NOT NULL,
    expires_at INTEGER NOT NULL,
    FOREIGN KEY (player_uuid) REFERENCES kbp_players(uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS kbp_leaderboard_snapshots (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    metric TEXT NOT NULL, -- POINTS, TIERS
    created_at INTEGER NOT NULL,
    payload TEXT NOT NULL -- JSON stored as TEXT
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_kbp_passes_player ON kbp_passes(player_uuid);
CREATE INDEX IF NOT EXISTS idx_kbp_quest_progress_player ON kbp_quest_progress(player_uuid);
CREATE INDEX IF NOT EXISTS idx_kbp_rewards_claimed_player ON kbp_rewards_claimed(player_uuid);
CREATE INDEX IF NOT EXISTS idx_kbp_active_boosters_player ON kbp_active_boosters(player_uuid);
CREATE INDEX IF NOT EXISTS idx_kbp_active_boosters_expiry ON kbp_active_boosters(expires_at);
