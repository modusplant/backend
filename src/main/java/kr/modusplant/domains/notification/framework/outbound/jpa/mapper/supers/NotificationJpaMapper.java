package kr.modusplant.domains.notification.framework.outbound.jpa.mapper.supers;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.notification.domain.aggregate.Notification;
import kr.modusplant.domains.notification.framework.outbound.jpa.entity.NotificationEntity;

public interface NotificationJpaMapper {
    NotificationEntity toNotificationEntity(Notification notification, MemberEntity recipient);

    Notification toNotification(NotificationEntity notificationEntity);
}
