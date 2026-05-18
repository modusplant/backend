package kr.modusplant.domains.notification.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.notification.framework.out.jpa.entity.FcmTokenEntity;
import kr.modusplant.shared.enums.Platform;

public interface FcmTokenJpaMapper {

    FcmTokenEntity toFcmTokenEntity(String token, MemberEntity memberEntity, Platform platform);
}
