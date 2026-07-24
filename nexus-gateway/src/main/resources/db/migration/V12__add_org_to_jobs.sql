-- V12: Add organization_id to jobs for multi-tenant isolation
ALTER TABLE nexus_jobs.jobs
    ADD COLUMN IF NOT EXISTS organization_id UUID;

CREATE INDEX IF NOT EXISTS idx_jobs_organization_id ON nexus_jobs.jobs (organization_id);
