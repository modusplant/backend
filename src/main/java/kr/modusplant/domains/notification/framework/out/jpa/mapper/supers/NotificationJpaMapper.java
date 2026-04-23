package kr.modusplant.domains.notification.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.notification.domain.aggregate.Notification;
import kr.modusplant.framework.jpa.entity.CommNotificationEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;

public interface NotificationJpaMapper {
    CommNotificationEntity toNotificationEntity(Notification notification, SiteMemberEntity recipient);

    Notification toNotification(CommNotificationEntity notificationEntity);
}
