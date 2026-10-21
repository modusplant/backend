ALTER TABLE public.comm_comment
ADD COLUMN path_1_depth_value integer NOT NULL GENERATED ALWAYS AS (split_part(path, '.', 1)::integer) STORED,
ADD COLUMN path_2_depth_value integer GENERATED ALWAYS AS (NULLIF(split_part(path, '.', 2), '')::integer) STORED;

CREATE INDEX idx_comm_comment_post_ulid_path_depth_value
    ON public.comm_comment (post_ulid, path_1_depth_value, path_2_depth_value NULLS FIRST);
