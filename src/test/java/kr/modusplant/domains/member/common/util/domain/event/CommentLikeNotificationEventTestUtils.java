package kr.modusplant.domains.member.common.util.domain.event;

import kr.modusplant.domains.member.domain.event.CommentLikeEvent;

import static kr.modusplant.domains.notification.common.constant.NotificationConstant.*;

public interface CommentLikeNotificationEventTestUtils {
    CommentLikeEvent testCommentLikeEvent = CommentLikeEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID, TEST_NOTIFICATION_COMMENT_PATH_DEPTH3);
}
