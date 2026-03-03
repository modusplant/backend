ALTER TABLE public.comm_post_abu_rep
    ALTER COLUMN ver_num TYPE integer
    USING 0;

ALTER TABLE public.comm_comment_abu_rep
    ALTER COLUMN ver_num TYPE integer
    USING 0;