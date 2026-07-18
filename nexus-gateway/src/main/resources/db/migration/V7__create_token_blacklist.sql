-- Token blacklist is stored in Redis with TTL, not in PostgreSQL.
-- This migration creates an audit index to track revoked tokens
-- if Redis is unavailable during forensic analysis.
CREATE TABLE IF NOT EXISTS nexus_iam.revoked_tokens (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    jti         VARCHAR(512) NOT NULL UNIQUE,
    revoked_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    expires_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    reason      VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_revoked_tokens_jti ON nexus_iam.revoked_tokens(jti);
CREATE INDEX IF NOT EXISTS idx_revoked_tokens_expires ON nexus_iam.revoked_tokens(expires_at);
