package kr.modusplant.domains.notification.framework.outbound.jpa.entity.common.util;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.MemberEntityTestUtils;
import kr.modusplant.domains.notification.framework.outbound.jpa.entity.NotificationEntity;
import kr.modusplant.domains.notification.framework.outbound.jpa.entity.NotificationEntity.NotificationEntityBuilder;
import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.enums.NotificationStatusType;

import java.time.LocalDateTime;

import static kr.modusplant.domains.notification.common.constant.NotificationConstant.*;

public interface NotificationEntityTestUtils extends MemberEntityTestUtils {
    default NotificationEntityBuilder createPostNotificationEntityBuilder() {
        return NotificationEntity.builder()
                .actorId(TEST_NOTIFICATION_ACTOR_ID)
                .actorNickname(TEST_NOTIFICATION_ACTOR_NICKNAME)
                .action(NotificationActionType.POST_LIKED)
                .status(NotificationStatusType.UNREAD)
                .postUlid(TEST_NOTIFICATION_POST_ULID)
                .contentPreview(TEST_NOTIFICATION_COMMENT_PREVIEW)
                .createdAt(LocalDateTime.now());
    }

    default NotificationEntityBuilder createPostNotificationEntityBuilderWithUlid() {
        return NotificationEntity.builder()
                .ulid(TEST_NOTIFICATION_ULID)
                .actorId(TEST_NOTIFICATION_ACTOR_ID)
                .actorNickname(TEST_NOTIFICATION_ACTOR_NICKNAME)
                .action(NotificationActionType.POST_LIKED)
                .status(NotificationStatusType.UNREAD)
                .postUlid(TEST_NOTIFICATION_POST_ULID)
                .contentPreview(TEST_NOTIFICATION_COMMENT_PREVIEW)
                .createdAt(LocalDateTime.now());
    }

    default NotificationEntityBuilder createCommentNotificationEntityBuilder(NotificationActionType actionType) {
        return NotificationEntity.builder()
                .actorId(TEST_NOTIFICATION_ACTOR_ID)
                .actorNickname(TEST_NOTIFICATION_ACTOR_NICKNAME)
                .action(actionType)
                .status(NotificationStatusType.UNREAD)
                .postUlid(TEST_NOTIFICATION_POST_ULID)
                .commentPath(TEST_NOTIFICATION_COMMENT_PATH_DEPTH3)
                .contentPreview(TEST_NOTIFICATION_COMMENT_PREVIEW)
                .createdAt(LocalDateTime.now());
    }

    default NotificationEntityBuilder createCommentNotificationEntityBuilderWithUlid(NotificationActionType actionType) {
        return NotificationEntity.builder()
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
