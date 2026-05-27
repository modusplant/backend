package kr.modusplant.domains.notification.framework.outbound.jpa.mapper.supers;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.notification.framework.outbound.jpa.entity.FcmTokenEntity;
import kr.modusplant.shared.enums.Platform;

public interface FcmTokenJpaMapper {

    FcmTokenEntity toFcmTokenEntity(String token, MemberEntity memberEntity, Platform platform);
}
