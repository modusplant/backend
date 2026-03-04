CREATE TABLE "comm_comment_abu_rep" (
	"ulid"	            varchar(26)		NOT NULL,
	"memb_uuid"	        uuid		    NOT NULL,
	"post_ulid"	        varchar(26)		NOT NULL,
	"path"	            text		    NOT NULL,
	"checked_at"	    timestamp		NULL,
	"handled_at"	    timestamp		NULL,
	"created_at"	    timestamp		NOT NULL,
    "last_modified_at"	timestamp		NOT NULL,
	"ver_num"	        timestamp		NOT NULL
);

ALTER TABLE "comm_comment_abu_rep" ADD CONSTRAINT "PK_COMM_COMMENT_ABU_REP" PRIMARY KEY (
	"ulid"
);