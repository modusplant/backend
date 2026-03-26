package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.notification.domain.exception.InvalidValueException;
import kr.modusplant.domains.notification.domain.exception.enums.NotificationErrorCode;
import kr.modusplant.domains.notification.domain.vo.NotificationId;
import kr.modusplant.domains.notification.domain.vo.NotificationStatus;
import kr.modusplant.domains.notification.domain.vo.RecipientId;
import kr.modusplant.domains.notification.framework.out.jpa.repository.supers.NotificationJpaRepository;
import kr.modusplant.domains.notification.usecase.port.repository.NotificationRepository;
import kr.modusplant.framework.jpa.entity.CommNotificationEntity;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryJpaAdapter implements NotificationRepository {
    private final NotificationJpaRepository notificationJpaRepository;
    private final SiteMemberJpaRepository siteMemberJpaRepository;

    @Override
    public void markAsRead(NotificationId notificationId,RecipientId recipientId) {
        CommNotificationEntity notificationEntity = notificationJpaRepository.findByUlidAndRecipient(
                notificationId.getValue(),
                siteMemberJpaRepository.findByUuid(recipientId.getValue()).orElseThrow(() -> new InvalidValueException(NotificationErrorCode.INVALID_RECIPIENT_ID))
        ).orElseThrow(() -> new InvalidValueException(NotificationErrorCode.INVALID_NOTIFICATION_ID));
        notificationEntity.read();
        notificationJpaRepository.save(notificationEntity);
    }

    @Override
    public void markAllAsRead(RecipientId recipientId) {
        notificationJpaRepository.updateUnreadStatus(recipientId.getValue());
    }

    @Override
    public long countByRecipientIdAndStatus(RecipientId recipientId, NotificationStatus status) {
        return notificationJpaRepository.countByRecipientAndStatus(
                siteMemberJpaRepository.findByUuid(recipientId.getValue()).orElseThrow(() -> new InvalidValueException(NotificationErrorCode.INVALID_RECIPIENT_ID)),
                status.getStatus()
        );
    }

}
