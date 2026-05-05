package kr.modusplant.domains.notification.usecase.port.mapper;

import kr.modusplant.domains.notification.domain.aggregate.Notification;
import kr.modusplant.domains.notification.domain.vo.*;
import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import kr.modusplant.domains.notification.usecase.response.NotificationResponse;

public interface NotificationMapper {
    NotificationResponse toNotificationResponse(NotificationReadModel readModel);

    Notification toPostNotification(RecipientId recipientId, Actor actor, NotificationAction action, PostId postId, ContentPreview contentPreview);

    Notification toCommentNotification(RecipientId recipientId, Actor actor, NotificationAction action, PostId postId, CommentPath commentPath, ContentPreview contentPreview);
}
