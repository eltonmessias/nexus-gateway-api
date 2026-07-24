-- V11: Replace teamId with organizationId and add key to projects

ALTER TABLE nexus_iam.projects
    DROP COLUMN IF EXISTS team_id;

ALTER TABLE nexus_iam.projects
    ADD COLUMN IF NOT EXISTS organization_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
    ADD COLUMN IF NOT EXISTS key VARCHAR(50);

-- Make key unique and not null after backfill (existing rows get key = id)
UPDATE nexus_iam.projects SET key = id::text WHERE key IS NULL;

ALTER TABLE nexus_iam.projects
    ALTER COLUMN key SET NOT NULL;

ALTER TABLE nexus_iam.projects
    ADD CONSTRAINT uk_projects_key UNIQUE (key);

CREATE INDEX IF NOT EXISTS idx_projects_organization_id ON nexus_iam.projects (organization_id);
