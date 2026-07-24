-- V9: Create org_members table for organisation membership management

CREATE TABLE nexus_iam.org_members (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    user_id         UUID,
    name            VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    role            VARCHAR(20)  NOT NULL DEFAULT 'VIEWER',
    status          VARCHAR(20)  NOT NULL DEFAULT 'INVITED',
    joined_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uk_org_members_org_email UNIQUE (organization_id, email)
);

CREATE INDEX idx_org_members_organization_id ON nexus_iam.org_members (organization_id);
