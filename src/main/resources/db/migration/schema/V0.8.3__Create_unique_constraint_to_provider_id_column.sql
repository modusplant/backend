DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'UK_SITE_MEMBER_AUTH'
    ) THEN
        ALTER TABLE ONLY public.site_member_auth
            ADD CONSTRAINT "UK_SITE_MEMBER_AUTH" UNIQUE (provider_id);
    END IF;
END $$;