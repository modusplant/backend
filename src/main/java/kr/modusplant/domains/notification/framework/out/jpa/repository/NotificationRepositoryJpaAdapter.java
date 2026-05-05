package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.notification.domain.aggregate.Notification;
import kr.modusplant.domains.notification.domain.exception.InvalidValueException;
import kr.modusplant.domains.notification.domain.exception.enums.NotificationErrorCode;
import kr.modusplant.domains.notification.domain.vo.NotificationId;
import kr.modusplant.domains.notification.domain.vo.NotificationStatus;
import kr.modusplant.domains.notification.domain.vo.RecipientId;
import kr.modusplant.domains.notification.framework.out.jpa.mapper.supers.NotificationJpaMapper;
import kr.modusplant.domains.notification.framework.out.jpa.repository.supers.NotificationJpaRepository;
import kr.modusplant.domains.notification.usecase.port.repository.NotificationRepository;
import kr.modusplant.framework.jpa.entity.CommNotificationEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryJpaAdapter implements NotificationRepository {
    private final NotificationJpaRepository notificationJpaRepository;
    private final SiteMemberJpaRepository siteMemberJpaRepository;
    private final NotificationJpaMapper notificationJpaMapper;

    @Override
    public void markAsRead(NotificationId notificationId,RecipientId recipientId) {
        int count = notificationJpaRepository.updateUnreadStatusById(notificationId.getValue(), recipientId.getValue());
        if (count == 0) {
            throw new InvalidValueException(NotificationErrorCode.INVALID_NOTIFICATION_ID);
        }
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

    @Override
    public Notification saveWithLimit(Notification notification,int limit) {
        // 알림 저장
        SiteMemberEntity recipient = siteMemberJpaRepository.findByUuid(notification.getRecipientId().getValue())
                        .orElseThrow(() -> new InvalidValueException(NotificationErrorCode.INVALID_RECIPIENT_ID));
        CommNotificationEntity notificationEntity = notificationJpaRepository.save(notificationJpaMapper.toNotificationEntity(notification,recipient));
        // limit 초과 시 가장 오래된 알림 삭제
        notificationJpaRepository.findUlidsByRecipientId(recipient.getUuid(), PageRequest.of(limit-1,1))
                .stream().findFirst()
                .ifPresent(cutoffUlid -> notificationJpaRepository.deleteByRecipientIdAndUlidBefore(recipient.getUuid(),cutoffUlid));
        return notificationJpaMapper.toNotification(notificationEntity);
    }

}
