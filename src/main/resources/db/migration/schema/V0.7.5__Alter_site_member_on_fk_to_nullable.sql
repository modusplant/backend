ALTER TABLE public.comm_post ALTER COLUMN auth_memb_uuid DROP NOT NULL;
ALTER TABLE public.comm_post_archive ALTER COLUMN auth_memb_uuid DROP NOT NULL;
ALTER TABLE public.comm_post_abu_rep ALTER COLUMN memb_uuid DROP NOT NULL;
ALTER TABLE public.comm_comment ALTER COLUMN auth_memb_uuid DROP NOT NULL;
ALTER TABLE public.comm_comment_abu_rep ALTER COLUMN memb_uuid DROP NOT NULL;
ALTER TABLE public.prop_bug_rep ALTER COLUMN memb_uuid DROP NOT NULL;