package kr.modusplant.domains.notification.common.util.domain.vo;

import kr.modusplant.domains.notification.domain.vo.Actor;

import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_ACTOR_ID;
import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_ACTOR_NICKNAME;

public interface ActorTestUtils {
    Actor testActor = Actor.fromUuidWithNickname(TEST_NOTIFICATION_ACTOR_ID,TEST_NOTIFICATION_ACTOR_NICKNAME);
    Actor testActor2 = Actor.fromUuidWithNickname(UUID.randomUUID(), "ACTOR2");
}
