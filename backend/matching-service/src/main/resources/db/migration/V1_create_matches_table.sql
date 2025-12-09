-- Create matches table to store links between users and jobs
CREATE TABLE IF NOT EXISTS matches (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    score DOUBLE PRECISION NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'SUGGESTED',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Index for quick lookups of a user's matches
CREATE INDEX IF NOT EXISTS idx_matches_user_id ON matches (user_id);
-- Index to sort by score then recency for a given user
CREATE INDEX IF NOT EXISTS idx_matches_user_score_created ON matches (user_id, score DESC, created_at DESC);