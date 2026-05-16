package kr.modusplant.domains.notification.framework.out.jpa.mapper;

import kr.modusplant.domains.notification.framework.out.jpa.mapper.supers.FcmTokenJpaMapper;
import kr.modusplant.framework.jpa.entity.FcmTokenEntity;
import kr.modusplant.framework.jpa.entity.MemberEntity;
import kr.modusplant.shared.enums.Platform;
import org.springframework.stereotype.Component;

@Component
public class FcmTokenJpaMapperImpl implements FcmTokenJpaMapper {

    @Override
    public FcmTokenEntity toFcmTokenEntity(String token, MemberEntity memberEntity, Platform platform) {
        return FcmTokenEntity.builder()
                .member(memberEntity)
                .token(token)
                .platform(platform)
                .build();
    }
}
