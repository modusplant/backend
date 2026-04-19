package kr.modusplant.domains.notification.common.util.domain.vo;

import kr.modusplant.domains.notification.domain.vo.NotificationId;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_ULID;

public interface NotificationIdTestUtils {
    NotificationId testNotificationId = NotificationId.create(TEST_NOTIFICATION_ULID);
}
