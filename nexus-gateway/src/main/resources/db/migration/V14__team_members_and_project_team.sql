-- V14: Add team_id to projects and create team_members join table

ALTER TABLE nexus_iam.projects
    ADD COLUMN IF NOT EXISTS team_id UUID REFERENCES nexus_iam.teams(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_projects_team_id ON nexus_iam.projects (team_id);

CREATE TABLE IF NOT EXISTS nexus_iam.team_members (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    team_id       UUID NOT NULL REFERENCES nexus_iam.teams(id) ON DELETE CASCADE,
    org_member_id UUID NOT NULL REFERENCES nexus_iam.org_members(id) ON DELETE CASCADE,
    joined_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uk_team_members_team_member UNIQUE (team_id, org_member_id)
);

CREATE INDEX IF NOT EXISTS idx_team_members_team_id       ON nexus_iam.team_members (team_id);
CREATE INDEX IF NOT EXISTS idx_team_members_org_member_id ON nexus_iam.team_members (org_member_id);
