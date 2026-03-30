package kr.modusplant.domains.notification.common.util.domain.vo;

import kr.modusplant.domains.notification.domain.vo.NotificationAction;
import kr.modusplant.shared.enums.NotificationActionType;

public interface NotificationActionTestUtils {
    NotificationAction testNotificationActionPostLiked = NotificationAction.create(NotificationActionType.POST_LIKED);
    NotificationAction testNotificationActionCommentLiked = NotificationAction.create(NotificationActionType.COMMENT_LIKED);
    NotificationAction testNotificationActionCommentAdded = NotificationAction.create(NotificationActionType.COMMENT_ADDED);
    NotificationAction testNotificationActionCommentReplyAdded = NotificationAction.create(NotificationActionType.COMMENT_REPLY_ADDED);
}
