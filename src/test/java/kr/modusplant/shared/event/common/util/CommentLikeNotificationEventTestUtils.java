package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.CommentLikeNotificationEvent;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.*;

public interface CommentLikeNotificationEventTestUtils {
    CommentLikeNotificationEvent testCommentLikeNotificationEvent = CommentLikeNotificationEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID, TEST_NOTIFICATION_COMMENT_PATH_DEPTH3);
}
