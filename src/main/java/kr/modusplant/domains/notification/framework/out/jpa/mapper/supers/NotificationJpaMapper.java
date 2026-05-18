package kr.modusplant.domains.notification.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.notification.domain.aggregate.Notification;
import kr.modusplant.domains.notification.framework.out.jpa.entity.NotificationEntity;

public interface NotificationJpaMapper {
    NotificationEntity toNotificationEntity(Notification notification, MemberEntity recipient);

    Notification toNotification(NotificationEntity notificationEntity);
}
