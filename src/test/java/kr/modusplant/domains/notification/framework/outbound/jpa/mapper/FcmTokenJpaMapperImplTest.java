package kr.modusplant.domains.notification.framework.outbound.jpa.mapper;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.MemberEntityTestUtils;
import kr.modusplant.domains.notification.framework.outbound.jpa.entity.FcmTokenEntity;
import kr.modusplant.domains.notification.framework.outbound.jpa.mapper.supers.FcmTokenJpaMapper;
import kr.modusplant.shared.enums.Platform;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.notification.common.constant.FcmTokenConstant.TEST_FCM_TOKEN_WEB;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FcmTokenJpaMapperImplTest implements MemberEntityTestUtils {
    private final FcmTokenJpaMapper fcmTokenJpaMapper = new FcmTokenJpaMapperImpl();

    @Test
    @DisplayName("")
    void testToFcmTokenEntity_givenTokenAndSiteMemberEntityAndPlatform_willReturnFcmTokenEntity() {
        // given
        String token = TEST_FCM_TOKEN_WEB;
        MemberEntity memberEntity = createMemberBasicAdminEntityWithUuid();

        // when
        FcmTokenEntity result = fcmTokenJpaMapper.toFcmTokenEntity(token, memberEntity, Platform.WEB);

        // then
        assertEquals(result.getToken(), token);
        assertEquals(result.getMember(),memberEntity);
        assertEquals(result.getPlatform(), Platform.WEB);
    }

}