-- DDL
CREATE TABLE comm_notification (
    ulid            VARCHAR(26)     NOT NULL,    -- ULID (PK)
    recipient_id    UUID            NOT NULL,   -- 알림 수신자
    actor_id        UUID            NOT NULL,   -- 알림 행위자
    actor_nickname  VARCHAR(40)     NOT NULL,   -- 알림 행위자 닉네임
    action          VARCHAR(30)     NOT NULL,   -- POST_LIKED | COMMENT_LIKED | COMMENT_ADDED | COMMENT_REPLY_ADDED
    status          VARCHAR(10)     NOT NULL,   -- UNREAD | READ
    post_ulid       VARCHAR(26)     NOT NULL,   -- 연관 게시글 (항상 존재)
    comment_path    text            NULL,       -- 연관 댓글 경로 (POST_LIKED만 NULL)
    content_preview  VARCHAR(100)   NULL,       -- 미리보기 텍스트
    created_at       TIMESTAMP       NOT NULL
);

ALTER TABLE comm_notification ADD CONSTRAINT "PK_COMM_NOTIFICATION" PRIMARY KEY (ulid);

ALTER TABLE comm_notification ADD CONSTRAINT "CHK_COMM_NOTIFICATION_ACTION" CHECK (action IN ('POST_LIKED', 'COMMENT_LIKED', 'COMMENT_ADDED', 'COMMENT_REPLY_ADDED'));
ALTER TABLE comm_notification ADD CONSTRAINT "CHK_COMM_NOTIFICATION_STATUS" CHECK (status IN ('UNREAD', 'READ'));
ALTER TABLE comm_notification ADD CONSTRAINT "CHK_COMM_NOTIFICATION_COMMENT_PATH" CHECK (
    (action = 'POST_LIKED' AND comment_path IS NULL)
        OR (action <> 'POST_LIKED' AND comment_path IS NOT NULL)
    );

CREATE INDEX "IDX_NOTIFICATION_RECIPIENT_STATUS_ID" ON comm_notification (recipient_id, status, ulid DESC);
CREATE INDEX "IDX_NOTIFICATION_RECIPIENT" ON comm_notification (recipient_id);