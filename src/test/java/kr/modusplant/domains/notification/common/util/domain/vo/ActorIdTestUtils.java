package kr.modusplant.domains.notification.common.util.domain.vo;

import kr.modusplant.domains.notification.domain.vo.ActorId;

import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_ACTOR_ID;

public interface ActorIdTestUtils {
    ActorId testActorId = ActorId.fromUuid(TEST_NOTIFICATION_ACTOR_ID);
    ActorId testActorId2 = ActorId.fromUuid(UUID.randomUUID());
}
