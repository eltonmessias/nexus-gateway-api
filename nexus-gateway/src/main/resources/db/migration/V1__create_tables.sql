CREATE TABLE IF NOT EXISTS flags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    flag_key VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL DEFAULT 'default',
    environment VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT uk_flag_key_environment UNIQUE (flag_key, environment)
    );

CREATE TABLE IF NOT EXISTS jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(255) NOT NULL,
    payload JSONB,
    priority INTEGER NOT NULL DEFAULT 1,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_jobs_status ON jobs(status);
CREATE INDEX IF NOT EXISTS idx_flags_key_env ON flags(flag_key, environment);