ALTER TABLE public.comm_post_abu_rep DROP COLUMN handled_at;
ALTER TABLE public.comm_post_abu_rep DROP COLUMN last_modified_at;
ALTER TABLE public.comm_post_abu_rep DROP COLUMN ver_num;

ALTER TABLE public.comm_comment_abu_rep DROP COLUMN handled_at;
ALTER TABLE public.comm_comment_abu_rep DROP COLUMN last_modified_at;
ALTER TABLE public.comm_comment_abu_rep DROP COLUMN ver_num;