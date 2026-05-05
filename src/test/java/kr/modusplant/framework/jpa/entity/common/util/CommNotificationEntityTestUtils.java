package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.CommNotificationEntity;
import kr.modusplant.framework.jpa.entity.CommNotificationEntity.NotificationBuilder;
import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.enums.NotificationStatusType;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.*;

public interface CommNotificationEntityTestUtils extends SiteMemberEntityTestUtils {
    default NotificationBuilder createPostNotificationEntityBuilder() {
        return CommNotificationEntity.builder()
                .actorId(TEST_NOTIFICATION_ACTOR_ID)
                .actorNickname(TEST_NOTIFICATION_ACTOR_NICKNAME)
                .action(NotificationActionType.POST_LIKED)
                .status(NotificationStatusType.UNREAD)
                .postUlid(TEST_NOTIFICATION_POST_ULID)
                .contentPreview(TEST_NOTIFICATION_COMMENT_PREVIEW)
                .createdAt(LocalDateTime.now());
    }

    default NotificationBuilder createPostNotificationEntityBuilderWithUlid() {
        return CommNotificationEntity.builder()
                .ulid(TEST_NOTIFICATION_ULID)
                .actorId(TEST_NOTIFICATION_ACTOR_ID)
                .actorNickname(TEST_NOTIFICATION_ACTOR_NICKNAME)
                .action(NotificationActionType.POST_LIKED)
                .status(NotificationStatusType.UNREAD)
                .postUlid(TEST_NOTIFICATION_POST_ULID)
                .contentPreview(TEST_NOTIFICATION_COMMENT_PREVIEW)
                .createdAt(LocalDateTime.now());
    }

    default NotificationBuilder createCommentNotificationEntityBuilder(NotificationActionType actionType) {
        return CommNotificationEntity.builder()
                .actorId(TEST_NOTIFICATION_ACTOR_ID)
                .actorNickname(TEST_NOTIFICATION_ACTOR_NICKNAME)
                .action(actionType)
                .status(NotificationStatusType.UNREAD)
                .postUlid(TEST_NOTIFICATION_POST_ULID)
                .commentPath(TEST_NOTIFICATION_COMMENT_PATH_DEPTH3)
                .contentPreview(TEST_NOTIFICATION_COMMENT_PREVIEW)
                .createdAt(LocalDateTime.now());
    }

    default NotificationBuilder createCommentNotificationEntityBuilderWithUlid(NotificationActionType actionType) {
        return CommNotificationEntity.builder()
                .ulid(TEST_NOTIFICATION_ULID)
                .actorId(TEST_NOTIFICATION_ACTOR_ID)
                .actorNickname(TEST_NOTIFICATION_ACTOR_NICKNAME)
                .action(actionType)
                .status(NotificationStatusType.UNREAD)
                .postUlid(TEST_NOTIFICATION_POST_ULID)
                .commentPath(TEST_NOTIFICATION_COMMENT_PATH_DEPTH3)
                .contentPreview(TEST_NOTIFICATION_COMMENT_PREVIEW)
                .createdAt(LocalDateTime.now());
    }
}
