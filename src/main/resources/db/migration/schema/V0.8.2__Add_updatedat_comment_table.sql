ALTER TABLE public.comm_comment
    ADD COLUMN updated_at timestamp without time zone;

UPDATE public.comm_comment
    SET updated_at = created_at;

ALTER TABLE public.comm_comment
    ALTER COLUMN updated_at SET NOT NULL;