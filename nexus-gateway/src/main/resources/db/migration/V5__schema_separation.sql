-- Create schemas
CREATE SCHEMA IF NOT EXISTS nexus_iam;
CREATE SCHEMA IF NOT EXISTS nexus_flags;
CREATE SCHEMA IF NOT EXISTS nexus_jobs;

-- Move IAM tables
ALTER TABLE public.organizations SET SCHEMA nexus_iam;
ALTER TABLE public.users SET SCHEMA nexus_iam;
ALTER TABLE public.teams SET SCHEMA nexus_iam;
ALTER TABLE public.projects SET SCHEMA nexus_iam;
ALTER TABLE public.audit_logs SET SCHEMA nexus_iam;

-- api_clients (created by ddl-auto, move if exists)
DO $$ BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'api_clients') THEN
        ALTER TABLE public.api_clients SET SCHEMA nexus_iam;
    ELSE
        CREATE TABLE nexus_iam.api_clients (
            id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
            name             VARCHAR(255) NOT NULL,
            project_id       UUID NOT NULL REFERENCES nexus_iam.projects(id),
            organization_id  UUID NOT NULL REFERENCES nexus_iam.organizations(id),
            client_id        VARCHAR(255) NOT NULL UNIQUE,
            api_key_hash     VARCHAR(255) NOT NULL,
            rate_limit_rpm   INTEGER NOT NULL DEFAULT 100,
            rate_limit_burst INTEGER NOT NULL DEFAULT 20,
            active           BOOLEAN NOT NULL DEFAULT true,
            created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
            updated_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
        );
    END IF;
END $$;

-- Move flags and jobs tables
ALTER TABLE public.flags SET SCHEMA nexus_flags;
ALTER TABLE public.jobs SET SCHEMA nexus_jobs;
