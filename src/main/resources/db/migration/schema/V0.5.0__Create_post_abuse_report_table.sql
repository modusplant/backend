CREATE TABLE "comm_post_abu_rep" (
	"ulid"	            varchar(26)		NOT NULL,
	"memb_uuid"	        uuid		    NOT NULL,
	"post_ulid"	        varchar(26)		NOT NULL,
	"checked_at"	    timestamp		NULL,
	"handled_at"	    timestamp		NULL,
	"created_at"	    timestamp		NOT NULL,
	"last_modified_at"	timestamp		NOT NULL,
	"ver_num"	        timestamp		NOT NULL
);

ALTER TABLE "comm_post_abu_rep" ADD CONSTRAINT "PK_COMM_POST_ABU_REP" PRIMARY KEY (
	"ulid"
);