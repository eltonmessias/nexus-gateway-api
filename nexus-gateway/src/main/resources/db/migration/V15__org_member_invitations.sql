-- Token-based invitations: an invited member sets their own password via a link.
ALTER TABLE nexus_iam.org_members
    ADD COLUMN IF NOT EXISTS invite_token       VARCHAR(128),
    ADD COLUMN IF NOT EXISTS invite_expires_at  TIMESTAMP WITH TIME ZONE;

CREATE UNIQUE INDEX IF NOT EXISTS uk_org_members_invite_token
    ON nexus_iam.org_members (invite_token);
