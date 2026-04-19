CREATE TABLE public.prop_bug_rep (
    uuid uuid NOT NULL,
    memb_uuid uuid NOT NULL,
    title character varying(60) NOT NULL,
    content character varying(600) NOT NULL,
    image_path character varying(255),
    checked_at timestamp without time zone,
    handled_at timestamp without time zone,
    created_at timestamp without time zone NOT NULL,
    last_modified_at timestamp without time zone NOT NULL,
    ver_num integer NOT NULL
);