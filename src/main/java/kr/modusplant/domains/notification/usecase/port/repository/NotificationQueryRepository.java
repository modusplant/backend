package kr.modusplant.domains.notification.usecase.port.repository;

import kr.modusplant.domains.notification.domain.enums.NotificationStatusType;
import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;

import java.util.List;
import java.util.UUID;

;

public interface NotificationQueryRepository {
    List<NotificationReadModel> findByStatusWithCursor(NotificationStatusType status, UUID currentMemberUuid, String cursorUlid, int size);
}
