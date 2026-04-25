CREATE TABLE public.prop_bug_rep_archive (
    ulid varchar(26) PRIMARY KEY,
    memb_uuid uuid NULL,
    title character varying(60) NOT NULL,
    content character varying(600) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    archived_at timestamp without time zone NOT NULL,
    last_modified_at timestamp without time zone NOT NULL
);