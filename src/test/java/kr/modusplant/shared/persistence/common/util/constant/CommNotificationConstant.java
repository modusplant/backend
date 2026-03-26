package kr.modusplant.shared.persistence.common.util.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommNotificationConstant {
    // String
    public static final String TEST_NOTIFICATION_ACTOR_NICKNAME = "actor1";
    public static final String TEST_NOTIFICATION_POST_PREVIEW = "post_title_preview";
    public static final String TEST_NOTIFICATION_COMMENT_PREVIEW = "comment_preview";
    public static final String TEST_NOTIFICATION_COMMENT_PATH = "1.4.1";

    // UUID
    public static final UUID TEST_NOTIFICATION_RECIPIENT_ID = UUID.fromString("6ba6176c-bbc5-4767-9a25-598631918365");
    public static final UUID TEST_NOTIFICATION_ACTOR_ID = UUID.fromString("d6b716f1-60f7-4c79-aeaf-37037101f126");

    // ULID
    public static final String TEST_NOTIFICATION_ULID = "01K59D7R5ZT51X9HVZXGK4A6WN";
    public static final String TEST_NOTIFICATION_POST_ULID = "71K59D7R5ZT51X9HVZXGK4A6WN";
}
