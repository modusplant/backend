CREATE INDEX idx_comm_post_auth_memb_uuid ON public.comm_post (auth_memb_uuid);
CREATE INDEX idx_comm_comment_auth_memb_uuid ON public.comm_comment (auth_memb_uuid);
CREATE INDEX idx_comm_post_like_memb_uuid ON public.comm_post_like (memb_uuid);
CREATE INDEX idx_comm_comment_like_memb_uuid ON public.comm_comment_like (memb_uuid);
CREATE INDEX idx_comm_post_bookmark_memb_uuid ON public.comm_post_bookmark (memb_uuid);
CREATE INDEX idx_refresh_token_refresh_token ON public.refresh_token (refresh_token);
