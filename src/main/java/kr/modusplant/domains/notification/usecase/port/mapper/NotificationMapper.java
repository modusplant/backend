package kr.modusplant.domains.notification.usecase.port.mapper;

import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import kr.modusplant.domains.notification.usecase.response.NotificationResponse;

public interface NotificationMapper {
    NotificationResponse toNotificationResponse(NotificationReadModel readModel);
}
