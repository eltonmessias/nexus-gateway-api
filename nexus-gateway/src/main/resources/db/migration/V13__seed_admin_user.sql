-- Restore unique constraint on email in nexus_iam schema (may have been lost during schema migration)
DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint c
        JOIN pg_class t ON t.oid = c.conrelid
        JOIN pg_namespace n ON n.oid = t.relnamespace
        WHERE c.contype = 'u'
          AND t.relname = 'users'
          AND n.nspname = 'nexus_iam'
          AND c.conkey = ARRAY(
              SELECT a.attnum FROM pg_attribute a
              WHERE a.attrelid = t.oid AND a.attname = 'email'
          )
    ) THEN
        ALTER TABLE nexus_iam.users ADD CONSTRAINT users_email_key UNIQUE (email);
    END IF;
END $$;
