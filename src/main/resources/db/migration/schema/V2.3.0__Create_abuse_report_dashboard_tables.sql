CREATE TABLE comm_post_abuse_report_dashboard (
    post_ulid VARCHAR(26) PRIMARY KEY,
    report_count INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    first_reported_at TIMESTAMP NOT NULL,
    last_reported_at TIMESTAMP NOT NULL,
    ver_num INT NOT NULL
);

CREATE TABLE comm_comment_abuse_report_dashboard (
    post_ulid VARCHAR(26),
    path text,
    report_count INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    first_reported_at TIMESTAMP NOT NULL,
    last_reported_at TIMESTAMP NOT NULL,
    ver_num INT NOT NULL
);

ALTER TABLE comm_comment_abuse_report_dashboard ADD CONSTRAINT "PK_COMM_COMMENT_ABUSE_REPORT_DASHBOARD" PRIMARY KEY (
	"post_ulid", "path"
);