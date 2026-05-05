package kr.modusplant.domains.notification.framework.out.jpa.mapper;

import kr.modusplant.domains.notification.framework.out.jpa.mapper.supers.FcmTokenJpaMapper;
import kr.modusplant.framework.jpa.entity.FcmTokenEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.shared.enums.Platform;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.FcmTokenConstant.TEST_FCM_TOKEN_WEB;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FcmTokenJpaMapperImplTest implements SiteMemberEntityTestUtils {
    private final FcmTokenJpaMapper fcmTokenJpaMapper = new FcmTokenJpaMapperImpl();

    @Test
    @DisplayName("")
    void testToFcmTokenEntity_givenTokenAndSiteMemberEntityAndPlatform_willReturnFcmTokenEntity() {
        // given
        String token = TEST_FCM_TOKEN_WEB;
        SiteMemberEntity memberEntity = createMemberBasicAdminEntityWithUuid();

        // when
        FcmTokenEntity result = fcmTokenJpaMapper.toFcmTokenEntity(token, memberEntity, Platform.WEB);

        // then
        assertEquals(result.getToken(), token);
        assertEquals(result.getMember(),memberEntity);
        assertEquals(result.getPlatform(), Platform.WEB);
    }

}