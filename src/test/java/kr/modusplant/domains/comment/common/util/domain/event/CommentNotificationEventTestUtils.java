package kr.modusplant.domains.comment.common.util.domain.event;

import kr.modusplant.domains.comment.domain.event.CommentRegisterEvent;

import static kr.modusplant.domains.notification.common.constant.NotificationConstant.*;

public interface CommentNotificationEventTestUtils {
    CommentRegisterEvent testCommentRegisterEvent = CommentRegisterEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID, TEST_NOTIFICATION_COMMENT_PATH_DEPTH1, TEST_NOTIFICATION_COMMENT_PREVIEW);
    CommentRegisterEvent testCommentReplyNotificationEvent = CommentRegisterEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID, TEST_NOTIFICATION_COMMENT_PATH_DEPTH3, TEST_NOTIFICATION_COMMENT_PREVIEW);
}
