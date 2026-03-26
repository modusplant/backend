package kr.modusplant.domains.notification.common.util.domain.vo;

import kr.modusplant.domains.notification.domain.vo.RecipientId;

import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_RECIPIENT_ID;

public interface RecipientIdTestUtils {
    RecipientId testRecipientId = RecipientId.fromUuid(TEST_NOTIFICATION_RECIPIENT_ID);
    RecipientId testRecipientId2 = RecipientId.fromUuid(UUID.randomUUID());
}
