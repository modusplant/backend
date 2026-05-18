package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.CommentNotificationEvent;

import static kr.modusplant.domains.notification.common.constant.NotificationConstant.*;

public interface CommentNotificationEventTestUtils {
    CommentNotificationEvent testCommentNotificationEvent = CommentNotificationEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID, TEST_NOTIFICATION_COMMENT_PATH_DEPTH1, TEST_NOTIFICATION_COMMENT_PREVIEW);
    CommentNotificationEvent testCommentReplyNotificationEvent = CommentNotificationEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID, TEST_NOTIFICATION_COMMENT_PATH_DEPTH3, TEST_NOTIFICATION_COMMENT_PREVIEW);
}
