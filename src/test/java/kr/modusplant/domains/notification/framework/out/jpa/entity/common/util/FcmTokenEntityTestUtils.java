package kr.modusplant.domains.notification.framework.out.jpa.entity.common.util;

import kr.modusplant.domains.member.framework.out.jpa.entity.common.util.MemberEntityTestUtils;
import kr.modusplant.domains.notification.framework.out.jpa.entity.FcmTokenEntity;
import kr.modusplant.domains.notification.framework.out.jpa.entity.FcmTokenEntity.FcmTokenEntityBuilder;
import kr.modusplant.shared.enums.Platform;

import static kr.modusplant.domains.notification.common.constant.FcmTokenConstant.*;


public interface FcmTokenEntityTestUtils extends MemberEntityTestUtils {
    default FcmTokenEntityBuilder createWebFcmTokenEntityBuilder() {
        return FcmTokenEntity.builder()
                .token(TEST_FCM_TOKEN_WEB)
                .platform(Platform.WEB);
    }

    default FcmTokenEntityBuilder createAndroidFcmTokenEntityBuilder() {
        return FcmTokenEntity.builder()
                .token(TEST_FCM_TOKEN_ANDROID)
                .platform(Platform.ANDROID);
    }

    default FcmTokenEntityBuilder createIosFcmTokenEntityBuilder() {
        return FcmTokenEntity.builder()
                .token(TEST_FCM_TOKEN_IOS)
                .platform(Platform.IOS);
    }
}
