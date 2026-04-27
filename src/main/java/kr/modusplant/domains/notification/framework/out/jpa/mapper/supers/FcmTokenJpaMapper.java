package kr.modusplant.domains.notification.framework.out.jpa.mapper.supers;

import kr.modusplant.framework.jpa.entity.FcmTokenEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.enums.Platform;

public interface FcmTokenJpaMapper {

    FcmTokenEntity toFcmTokenEntity(String token, SiteMemberEntity memberEntity, Platform platform);
}
