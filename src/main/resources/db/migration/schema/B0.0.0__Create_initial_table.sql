CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE public.comm_comment (
    post_ulid character varying(26) NOT NULL,
    path text NOT NULL,
    auth_memb_uuid uuid NOT NULL,
    crea_memb_uuid uuid NOT NULL,
    like_count integer NOT NULL,
    content character varying(600) NOT NULL,
    is_deleted boolean NOT NULL,
    created_at timestamp without time zone NOT NULL
);

CREATE TABLE public.comm_comment_like (
    post_ulid character varying(26) NOT NULL,
    path text NOT NULL,
    memb_uuid uuid NOT NULL,
    created_at timestamp without time zone NOT NULL
);

CREATE TABLE public.comm_post (
    ulid character varying(26) NOT NULL,
    pri_cate_uuid uuid NOT NULL,
    seco_cate_uuid uuid NOT NULL,
    auth_memb_uuid uuid NOT NULL,
    crea_memb_uuid uuid NOT NULL,
    like_count integer NOT NULL,
    view_count integer NOT NULL,
    title character varying(60) NOT NULL,
    content jsonb NOT NULL,
    is_published boolean NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    published_at timestamp without time zone,
    ver integer NOT NULL
);

CREATE TABLE public.comm_post_archive (
    ulid character varying(26) NOT NULL,
    pri_cate_uuid uuid NOT NULL,
    seco_cate_uuid uuid NOT NULL,
    auth_memb_uuid uuid NOT NULL,
    crea_memb_uuid uuid NOT NULL,
    title character varying(60) NOT NULL,
    content jsonb NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    published_at timestamp without time zone
);

CREATE TABLE public.comm_post_bookmark (
    post_ulid character varying(26) NOT NULL,
    memb_uuid uuid NOT NULL,
    created_at timestamp without time zone NOT NULL
);

CREATE TABLE public.comm_post_like (
    post_ulid character varying(26) NOT NULL,
    memb_uuid uuid NOT NULL,
    created_at timestamp without time zone NOT NULL
);

CREATE TABLE public.comm_pri_cate (
    uuid uuid NOT NULL,
    category character varying(40) NOT NULL,
    "order" integer NOT NULL,
    created_at timestamp without time zone NOT NULL
);

CREATE TABLE public.comm_seco_cate (
    uuid uuid NOT NULL,
    pri_cate_uuid uuid NOT NULL,
    category character varying(40) NOT NULL,
    "order" integer NOT NULL,
    created_at timestamp without time zone NOT NULL
);

CREATE TABLE public.refresh_token (
    uuid uuid NOT NULL,
    memb_uuid uuid NOT NULL,
    refresh_token text NOT NULL,
    issued_at timestamp without time zone NOT NULL,
    expired_at timestamp without time zone NOT NULL
);

CREATE TABLE public.site_member (
    uuid uuid NOT NULL,
    nickname character varying(16) NOT NULL,
    birth_date date,
    is_active boolean NOT NULL,
    is_disabled_by_linking boolean NOT NULL,
    is_banned boolean NOT NULL,
    is_deleted boolean NOT NULL,
    logged_in_at timestamp without time zone,
    created_at timestamp without time zone NOT NULL,
    last_modified_at timestamp without time zone NOT NULL,
    ver_num integer NOT NULL
);

CREATE TABLE public.site_member_auth (
    uuid uuid NOT NULL,
    act_memb_uuid uuid NOT NULL,
    email character varying(255) NOT NULL,
    pw character varying(64),
    provider character varying(10) NOT NULL,
    provider_id text,
    lockout_until timestamp without time zone,
    last_modified_at timestamp without time zone NOT NULL,
    ver_num integer NOT NULL
);

CREATE TABLE public.site_member_prof (
    uuid uuid NOT NULL,
    intro character varying(60),
    image_path character varying(255),
    last_modified_at timestamp without time zone NOT NULL,
    ver_num integer NOT NULL
);

CREATE TABLE public.site_member_role (
    uuid uuid NOT NULL,
    role character varying(12) NOT NULL
);


CREATE TABLE public.site_member_term (
    uuid uuid NOT NULL,
    agreed_tou_ver character varying(10) NOT NULL,
    agreed_priv_poli_ver character varying(10) NOT NULL,
    agreed_ad_info_rece_ver character varying(10) NOT NULL,
    last_modified_at timestamp without time zone NOT NULL,
    ver_num integer NOT NULL
);

CREATE TABLE public.term (
    uuid uuid NOT NULL,
    name character varying(40) NOT NULL,
    content text NOT NULL,
    ver character varying(10) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    last_modified_at timestamp without time zone NOT NULL,
    ver_num integer NOT NULL
);

CREATE TABLE public.swear (
    uuid UUID NOT NULL,
    type character varying(10) NOT NULL,
    word character varying(10) NOT NULL,
    created_at TIMESTAMP without time zone NOT NULL
);

ALTER TABLE ONLY public.comm_comment
    ADD CONSTRAINT "PK_COMM_COMMENT" PRIMARY KEY (post_ulid, "path");

ALTER TABLE ONLY public.comm_comment_like
    ADD CONSTRAINT "PK_COMM_COMMENT_LIKE" PRIMARY KEY (post_ulid, "path", memb_uuid);

ALTER TABLE ONLY public.comm_post
    ADD CONSTRAINT "PK_COMM_POST" PRIMARY KEY (ulid);

ALTER TABLE ONLY public.comm_post_bookmark
    ADD CONSTRAINT "PK_COMM_POST_BOOKMARK" PRIMARY KEY (post_ulid, memb_uuid);

ALTER TABLE ONLY public.comm_post_like
    ADD CONSTRAINT "PK_COMM_POST_LIKE" PRIMARY KEY (post_ulid, memb_uuid);

ALTER TABLE ONLY public.comm_pri_cate
    ADD CONSTRAINT "PK_COMM_PRI_CATE" PRIMARY KEY (uuid);

ALTER TABLE ONLY public.comm_seco_cate
    ADD CONSTRAINT "PK_COMM_SECO_CATE" PRIMARY KEY (uuid);

ALTER TABLE ONLY public.refresh_token
    ADD CONSTRAINT "PK_REFRESH_TOKEN" PRIMARY KEY (uuid);

ALTER TABLE ONLY public.site_member
    ADD CONSTRAINT "PK_SITE_MEMBER" PRIMARY KEY (uuid);

ALTER TABLE ONLY public.site_member_auth
    ADD CONSTRAINT "PK_SITE_MEMBER_AUTH" PRIMARY KEY (uuid);

ALTER TABLE ONLY public.site_member_prof
    ADD CONSTRAINT "PK_SITE_MEMBER_PROF" PRIMARY KEY (uuid);

ALTER TABLE ONLY public.site_member_role
    ADD CONSTRAINT "PK_SITE_MEMBER_ROLE" PRIMARY KEY (uuid);

ALTER TABLE ONLY public.site_member_term
    ADD CONSTRAINT "PK_SITE_MEMBER_TERM" PRIMARY KEY (uuid);

ALTER TABLE ONLY public.term
    ADD CONSTRAINT "PK_TERM" PRIMARY KEY (uuid);

ALTER TABLE ONLY public.swear
    ADD CONSTRAINT "PK_SWEAR" PRIMARY KEY (uuid);

ALTER TABLE ONLY public.comm_post_archive
    ADD CONSTRAINT comm_post_archive_pkey PRIMARY KEY (ulid);

ALTER TABLE ONLY public.term
    ADD CONSTRAINT "UK_TERM" UNIQUE ("name");

ALTER TABLE ONLY public.site_member_auth
    ADD CONSTRAINT "UK_SITE_MEMBER_AUTH" UNIQUE (provider_id);

ALTER TABLE ONLY public.site_member
    ADD CONSTRAINT "UK_SITE_MEMBER" UNIQUE (nickname);

ALTER TABLE ONLY public.comm_pri_cate
    ADD CONSTRAINT "UK_COMM_PRI_CATE" UNIQUE (category, "order");

ALTER TABLE ONLY public.swear
    ADD CONSTRAINT "UK_SWEAR" UNIQUE (word);