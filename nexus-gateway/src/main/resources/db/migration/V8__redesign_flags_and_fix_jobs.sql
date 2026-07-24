-- V8: Redesign flags table + add missing columns to jobs

-- Drop old flags table and recreate with new schema
-- (flagKey+value+environment → key+description+projectId+timestamps)
DROP TABLE IF EXISTS nexus_flags.flags;

CREATE TABLE nexus_flags.flags (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key         VARCHAR(255) NOT NULL,
    description TEXT,
    project_id  UUID NOT NULL,
    enabled     BOOLEAN NOT NULL DEFAULT false,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uk_flags_key UNIQUE (key)
);

CREATE INDEX idx_flags_project_id ON nexus_flags.flags (project_id);

-- Add missing columns to jobs table
ALTER TABLE nexus_jobs.jobs
    ADD COLUMN IF NOT EXISTS retries     INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS max_retries INTEGER NOT NULL DEFAULT 3,
    ADD COLUMN IF NOT EXISTS updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now();

-- Backfill created_at for any jobs where it may be null
UPDATE nexus_jobs.jobs SET created_at = now() WHERE created_at IS NULL;
