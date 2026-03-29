package kr.modusplant.domains.notification.usecase.port.repository;

import kr.modusplant.domains.notification.domain.vo.NotificationId;
import kr.modusplant.domains.notification.domain.vo.NotificationStatus;
import kr.modusplant.domains.notification.domain.vo.RecipientId;

public interface NotificationRepository {

    void markAsRead(NotificationId notificationId, RecipientId recipientId);

    void markAllAsRead(RecipientId recipientId);

    long countByRecipientIdAndStatus(RecipientId recipientId, NotificationStatus status);
}
