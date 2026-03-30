-- Add updated_at column to comm_comment table
-- Existing rows will be filled with created_at value

ALTER TABLE public.comm_comment
    ADD COLUMN updated_at timestamp without time zone NOT NULL DEFAULT NOW();

UPDATE public.comm_comment
    SET updated_at = created_at;

ALTER TABLE public.comm_comment
    ALTER COLUMN updated_at DROP DEFAULT;