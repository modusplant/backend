package kr.modusplant.domains.notification.common.util.usecase.response;

import kr.modusplant.domains.notification.usecase.response.NotificationResponse;
import kr.modusplant.shared.enums.ContentType;
import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.enums.NotificationStatusType;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.*;

public interface NotificationResponseTestUtils {
    LocalDateTime testDate = LocalDateTime.of(2026, 3, 20, 0, 0);

    NotificationResponse TEST_POST_LIKED_READ_NOTIFICATION_RESPONSE = new NotificationResponse(
            TEST_NOTIFICATION_ULID,
            TEST_NOTIFICATION_ACTOR_ID,
            TEST_NOTIFICATION_ACTOR_NICKNAME,
            NotificationActionType.POST_LIKED.name(),
            NotificationStatusType.READ.getValue(),
            TEST_NOTIFICATION_POST_ULID,
            null,
            ContentType.POST.getValue(),
            TEST_NOTIFICATION_POST_PREVIEW,
            testDate
    );

    NotificationResponse TEST_POST_LIKED_UNREAD_NOTIFICATION_RESPONSE = new NotificationResponse(
            TEST_NOTIFICATION_ULID,
            TEST_NOTIFICATION_ACTOR_ID,
            TEST_NOTIFICATION_ACTOR_NICKNAME,
            NotificationActionType.POST_LIKED.name(),
            NotificationStatusType.UNREAD.getValue(),
            TEST_NOTIFICATION_POST_ULID,
            null,
            ContentType.POST.getValue(),
            TEST_NOTIFICATION_POST_PREVIEW,
            testDate
    );

    NotificationResponse TEST_COMMENT_LIKED_READ_NOTIFICATION_RESPONSE = new NotificationResponse(
            TEST_NOTIFICATION_ULID,
            TEST_NOTIFICATION_ACTOR_ID,
            TEST_NOTIFICATION_ACTOR_NICKNAME,
            NotificationActionType.COMMENT_LIKED.name(),
            NotificationStatusType.READ.getValue(),
            TEST_NOTIFICATION_POST_ULID,
            TEST_NOTIFICATION_COMMENT_PATH,
            ContentType.COMMENT.getValue(),
            TEST_NOTIFICATION_COMMENT_PREVIEW,
            testDate
    );

    NotificationResponse TEST_COMMENT_ADDED_UNREAD_NOTIFICATION_RESPONSE = new NotificationResponse(
            TEST_NOTIFICATION_ULID,
            TEST_NOTIFICATION_ACTOR_ID,
            TEST_NOTIFICATION_ACTOR_NICKNAME,
            NotificationActionType.COMMENT_ADDED.name(),
            NotificationStatusType.UNREAD.getValue(),
            TEST_NOTIFICATION_POST_ULID,
            TEST_NOTIFICATION_COMMENT_PATH,
            ContentType.COMMENT.getValue(),
            TEST_NOTIFICATION_COMMENT_PREVIEW,
            testDate
    );

}
