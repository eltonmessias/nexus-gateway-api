ALTER TABLE nexus_iam.audit_logs
    ADD COLUMN IF NOT EXISTS actor_name VARCHAR(255);
