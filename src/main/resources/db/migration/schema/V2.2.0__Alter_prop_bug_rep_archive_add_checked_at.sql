ALTER TABLE public.prop_bug_rep_archive ADD COLUMN checked_at timestamp without time zone NULL;

UPDATE public.prop_bug_rep_archive SET checked_at = archived_at;

ALTER TABLE public.prop_bug_rep_archive ALTER COLUMN checked_at SET NOT NULL;