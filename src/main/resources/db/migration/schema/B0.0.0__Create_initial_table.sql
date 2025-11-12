CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE "term" (
	"uuid"	            uuid		    NOT NULL,
	"name"	            varchar(40)		NOT NULL,
	"content"	        text		    NOT NULL,
	"ver"	            varchar(10)		NOT NULL,
	"created_at"	    timestamp		NOT NULL,
	"last_modified_at"	timestamp	    NOT NULL,
    "ver_num"	        int		        NOT NULL
);

COMMENT ON COLUMN "term"."name" IS 'UNIQUE';

CREATE TABLE "refresh_token" (
	"uuid"	uuid		NOT NULL,
	"memb_uuid"	uuid		NOT NULL,
	"refresh_token"	text		NOT NULL,
	"issued_at"	timestamp		NOT NULL,
	"expired_at"	timestamp		NOT NULL
);

CREATE TABLE "site_member_role" (
	"uuid"	uuid		NOT NULL,
	"role"	varchar(12)		NOT NULL
);

CREATE TABLE "comm_comment_like" (
	"post_ulid"	varchar(26)		NOT NULL,
	"path"	text		NOT NULL,
	"memb_uuid"	uuid		NOT NULL,
	"created_at"	timestamp		NOT NULL
);

CREATE TABLE "comm_comment" (
	"post_ulid"	varchar(26)		NOT NULL,
	"path"	text		NOT NULL,
	"auth_memb_uuid"	uuid		NOT NULL,
	"crea_memb_uuid"	uuid		NOT NULL,
	"like_count"	int		NOT NULL,
	"content"	varchar(600)		NOT NULL,
	"is_deleted"	boolean		NOT NULL,
	"created_at"	timestamp		NOT NULL
);

CREATE TABLE "comm_pri_cate" (
	"uuid"	uuid		NOT NULL,
	"category"	varchar(40)		NOT NULL,
	"order"	int		NOT NULL,
	"created_at"	timestamp		NOT NULL
);

COMMENT ON COLUMN "comm_pri_cate"."category" IS 'UNIQUE';

COMMENT ON COLUMN "comm_pri_cate"."order" IS 'UNIQUE';

CREATE TABLE "comm_post" (
	"ulid"	varchar(26)		NOT NULL,
	"pri_cate_uuid"	uuid		NOT NULL,
	"seco_cate_uuid"	uuid		NOT NULL,
	"auth_memb_uuid"	uuid		NOT NULL,
	"crea_memb_uuid"	uuid		NOT NULL,
	"like_count"	int		NOT NULL,
	"view_count"	int		NOT NULL,
	"title"	varchar(60)		NOT NULL,
	"content"	jsonb		NOT NULL,
	"is_published"	boolean		NOT NULL,
	"created_at"	timestamp		NOT NULL,
	"updated_at"	timestamp		NOT NULL,
	"published_at" timestamp,
	"ver"	int		NOT NULL
);

CREATE TABLE comm_post_archive (
    "ulid"           varchar(26)  NOT NULL PRIMARY KEY,
    "pri_cate_uuid"  uuid         NOT NULL,
    "seco_cate_uuid" uuid         NOT NULL,
    "auth_memb_uuid" uuid         NOT NULL,
    "crea_memb_uuid" uuid         NOT NULL,
    "title"          varchar(60)  NOT NULL,
    "content"        jsonb        NOT NULL,
    "created_at"     timestamp    NOT NULL,
    "updated_at"     timestamp    NOT NULL,
    "published_at"   timestamp
);

CREATE TABLE "site_member_term" (
	"uuid"	uuid		NOT NULL,
	"agreed_tou_ver"	varchar(10)		NOT NULL,
	"agreed_priv_poli_ver"	varchar(10)		NOT NULL,
	"agreed_ad_info_rece_ver"	varchar(10)		NOT NULL,
	"last_modified_at"	timestamp		NOT NULL,
	"ver_num"	int		NOT NULL
);

CREATE TABLE "comm_post_like" (
	"post_ulid"	varchar(26)		NOT NULL,
	"memb_uuid"	uuid		NOT NULL,
	"created_at"	timestamp		NOT NULL
);

CREATE TABLE "comm_post_bookmark" (
	"post_ulid"	varchar(26)		NOT NULL,
	"memb_uuid"	uuid		NOT NULL,
	"created_at"	timestamp		NOT NULL
);

CREATE TABLE "site_member_auth" (
	"uuid"	uuid		NOT NULL,
	"act_memb_uuid"	uuid		NOT NULL,
	"email"	varchar(255)		NOT NULL,
	"pw"	varchar(64)		NULL,
	"provider"	varchar(10)		NOT NULL,
	"provider_id"	text		NULL,
	"lockout_until"	timestamp		NULL,
	"last_modified_at"	timestamp		NOT NULL,
	"ver_num"	int		NOT NULL
);

COMMENT ON COLUMN "site_member_auth"."provider_id" IS 'UNIQUE';

CREATE TABLE "comm_seco_cate" (
	"uuid"	uuid		NOT NULL,
	"pri_cate_uuid" uuid NOT NULL,
	"category"	varchar(40)		NOT NULL,
	"order"	int		NOT NULL,
	"created_at"	timestamp		NOT NULL
);

CREATE TABLE "site_member" (
	"uuid"	uuid		NOT NULL,
	"nickname"	varchar(16)		NOT NULL,
	"birth_date"	date		NULL,
	"is_active"	boolean		NOT NULL,
	"is_disabled_by_linking"	boolean		NOT NULL,
	"is_banned"	boolean		NOT NULL,
	"is_deleted"	boolean		NOT NULL,
	"logged_in_at"	timestamp		NULL,
	"created_at"	timestamp		NOT NULL,
	"last_modified_at"	timestamp		NOT NULL,
	"ver_num"	int		NOT NULL
);

COMMENT ON COLUMN "site_member"."nickname" IS 'UNIQUE';

CREATE TABLE "site_member_prof" (
	"uuid"	uuid		NOT NULL,
	"intro"	varchar(60)		NULL,
	"image_path"	varchar(255)		NULL,
	"last_modified_at"	timestamp		NOT NULL,
	"ver_num"	int		NOT NULL
);

ALTER TABLE "term" ADD CONSTRAINT "PK_TERM" PRIMARY KEY (
	"uuid"
);

ALTER TABLE "refresh_token" ADD CONSTRAINT "PK_REFRESH_TOKEN" PRIMARY KEY (
	"uuid"
);

ALTER TABLE "site_member_role" ADD CONSTRAINT "PK_SITE_MEMBER_ROLE" PRIMARY KEY (
	"uuid"
);

ALTER TABLE "prop_bug_rep" ADD CONSTRAINT "PK_PROP_BUG_REP" PRIMARY KEY (
	"uuid"
);

ALTER TABLE "comm_comment_like" ADD CONSTRAINT "PK_COMM_COMMENT_LIKE" PRIMARY KEY (
	"post_ulid",
	"path",
	"memb_uuid"
);

ALTER TABLE "comm_comment" ADD CONSTRAINT "PK_COMM_COMMENT" PRIMARY KEY (
	"post_ulid",
	"path"
);

ALTER TABLE "comm_pri_cate" ADD CONSTRAINT "PK_COMM_PRI_CATE" PRIMARY KEY (
	"uuid"
);

ALTER TABLE "comm_post" ADD CONSTRAINT "PK_COMM_POST" PRIMARY KEY (
	"ulid"
);

ALTER TABLE "site_member_term" ADD CONSTRAINT "PK_SITE_MEMBER_TERM" PRIMARY KEY (
	"uuid"
);

ALTER TABLE "comm_post_like" ADD CONSTRAINT "PK_COMM_POST_LIKE" PRIMARY KEY (
	"post_ulid",
	"memb_uuid"
);

ALTER TABLE "comm_post_bookmark" ADD CONSTRAINT "PK_COMM_POST_BOOKMARK" PRIMARY KEY (
	"post_ulid",
	"memb_uuid"
);

ALTER TABLE "site_member_auth" ADD CONSTRAINT "PK_SITE_MEMBER_AUTH" PRIMARY KEY (
	"uuid"
);

ALTER TABLE "comm_seco_cate" ADD CONSTRAINT "PK_COMM_SECO_CATE" PRIMARY KEY (
	"uuid"
);

ALTER TABLE "site_member" ADD CONSTRAINT "PK_SITE_MEMBER" PRIMARY KEY (
	"uuid"
);

ALTER TABLE "site_member_prof" ADD CONSTRAINT "PK_SITE_MEMBER_PROF" PRIMARY KEY (
	"uuid"
);