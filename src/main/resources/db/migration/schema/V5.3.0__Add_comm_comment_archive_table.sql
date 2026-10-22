CREATE TABLE public.comm_comment_archive (
    path text NOT NULL,
    post_ulid character varying(26) NOT NULL,
    auth_memb_uuid uuid NULL,
    content character varying(600) NOT NULL,
    is_deleted boolean NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    edited_at timestamp without time zone NULL,
    archived_at timestamp without time zone NOT NULL
);

ALTER TABLE ONLY public.comm_comment_archive
    ADD CONSTRAINT "PK_COMM_COMMENT_ARCHIVE" PRIMARY KEY (post_ulid, "path");
