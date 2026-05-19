package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.PostLikeNotificationEvent;

import static kr.modusplant.domains.notification.common.constant.NotificationConstant.TEST_NOTIFICATION_ACTOR_ID;
import static kr.modusplant.domains.notification.common.constant.NotificationConstant.TEST_NOTIFICATION_POST_ULID;

public interface PostLikeNotificationEventTestUtils {
    PostLikeNotificationEvent testPostLikeNotificationEvent = PostLikeNotificationEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID);
}
