package kr.modusplant.domains.notification.adapter.controller;

import kr.modusplant.domains.notification.domain.vo.NotificationId;
import kr.modusplant.domains.notification.domain.vo.NotificationStatus;
import kr.modusplant.domains.notification.domain.vo.RecipientId;
import kr.modusplant.domains.notification.usecase.port.mapper.NotificationMapper;
import kr.modusplant.domains.notification.usecase.port.repository.NotificationQueryRepository;
import kr.modusplant.domains.notification.usecase.port.repository.NotificationRepository;
import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import kr.modusplant.domains.notification.usecase.response.CursorPageResponse;
import kr.modusplant.domains.notification.usecase.response.NotificationResponse;
import kr.modusplant.shared.enums.NotificationStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;
    private final NotificationQueryRepository notificationQueryRepository;

    public CursorPageResponse<NotificationResponse> getNotifications(NotificationStatusType status, UUID currentMemberUuid, String lastUlid, int size) {
        List<NotificationReadModel> readModels = notificationQueryRepository.findByStatusWithCursor(status,currentMemberUuid,lastUlid,size);
        boolean hasNext = readModels.size() > size;
        List<NotificationResponse> responses = readModels.stream()
                .limit(size)
                .map(notificationMapper::toNotificationResponse)
                .toList();
        String nextUlid = hasNext && !responses.isEmpty() ? responses.get(responses.size() - 1).ulid() : null;
        return CursorPageResponse.of(responses, nextUlid, hasNext);
    }

    @Transactional
    public void readNotification(String notificationId, UUID currentMemberUuid) {
        notificationRepository.markAsRead(NotificationId.create(notificationId), RecipientId.fromUuid(currentMemberUuid));
    }

    @Transactional
    public void readAllNotifications(UUID currentMemberUuid) {
        notificationRepository.markAllAsRead(RecipientId.fromUuid(currentMemberUuid));
    }

    public Long countUnreadNotifications(UUID currentMemberUuid) {
        return notificationRepository.countByRecipientIdAndStatus(RecipientId.fromUuid(currentMemberUuid), NotificationStatus.unread());
    }

}
