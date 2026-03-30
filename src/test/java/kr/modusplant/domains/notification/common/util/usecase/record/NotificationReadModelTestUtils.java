package kr.modusplant.domains.notification.common.util.usecase.record;

import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.enums.NotificationStatusType;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.*;

public interface NotificationReadModelTestUtils {
    LocalDateTime testDate = LocalDateTime.of(2026, 3, 20, 0, 0);

    NotificationReadModel TEST_POST_LIKED_READ_NOTIFICATION_READ_MODEL = new NotificationReadModel(
            TEST_NOTIFICATION_ULID,
            TEST_NOTIFICATION_RECIPIENT_ID,
            TEST_NOTIFICATION_ACTOR_ID,
            TEST_NOTIFICATION_ACTOR_NICKNAME,
            NotificationActionType.POST_LIKED,
            NotificationStatusType.READ,
            TEST_NOTIFICATION_POST_ULID,
            null,
            TEST_NOTIFICATION_POST_PREVIEW,
            testDate
    );

    NotificationReadModel TEST_POST_LIKED_READ_NOTIFICATION_UNREAD_MODEL = new NotificationReadModel(
            TEST_NOTIFICATION_ULID,
            TEST_NOTIFICATION_RECIPIENT_ID,
            TEST_NOTIFICATION_ACTOR_ID,
            TEST_NOTIFICATION_ACTOR_NICKNAME,
            NotificationActionType.POST_LIKED,
            NotificationStatusType.UNREAD,
            TEST_NOTIFICATION_POST_ULID,
            null,
            TEST_NOTIFICATION_POST_PREVIEW,
            testDate
    );

    NotificationReadModel TEST_COMMENT_LIKED_READ_NOTIFICATION_READ_MODEL = new NotificationReadModel(
            TEST_NOTIFICATION_ULID,
            TEST_NOTIFICATION_RECIPIENT_ID,
            TEST_NOTIFICATION_ACTOR_ID,
            TEST_NOTIFICATION_ACTOR_NICKNAME,
            NotificationActionType.COMMENT_LIKED,
            NotificationStatusType.READ,
            TEST_NOTIFICATION_POST_ULID,
            TEST_NOTIFICATION_COMMENT_PATH,
            TEST_NOTIFICATION_COMMENT_PREVIEW,
            testDate
    );

    NotificationReadModel TEST_COMMENT_ADDED_UNREAD_NOTIFICATION_READ_MODEL = new NotificationReadModel(
            TEST_NOTIFICATION_ULID,
            TEST_NOTIFICATION_RECIPIENT_ID,
            TEST_NOTIFICATION_ACTOR_ID,
            TEST_NOTIFICATION_ACTOR_NICKNAME,
            NotificationActionType.COMMENT_ADDED,
            NotificationStatusType.UNREAD,
            TEST_NOTIFICATION_POST_ULID,
            TEST_NOTIFICATION_COMMENT_PATH,
            TEST_NOTIFICATION_COMMENT_PREVIEW,
            testDate
    );



}
