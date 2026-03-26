package kr.modusplant.domains.notification.common.util.domain.vo;

import kr.modusplant.domains.notification.domain.vo.NotificationStatus;

public interface NotificationStatusTestUtils {
    NotificationStatus testNotificationStatusRead = NotificationStatus.read();
    NotificationStatus testNotificationStatusUnread = NotificationStatus.unread();
}
