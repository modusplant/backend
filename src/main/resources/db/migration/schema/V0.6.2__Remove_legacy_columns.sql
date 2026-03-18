ALTER TABLE public.refresh_token
    DROP COLUMN issued_at;

ALTER TABLE public.site_member_auth
    DROP COLUMN lockout_until;

ALTER TABLE public.site_member
    DROP COLUMN birth_date;

ALTER TABLE public.site_member
    DROP COLUMN is_deleted;