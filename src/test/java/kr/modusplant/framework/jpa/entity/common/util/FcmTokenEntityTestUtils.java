package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.FcmTokenEntity;
import kr.modusplant.framework.jpa.entity.FcmTokenEntity.*;
import kr.modusplant.shared.enums.Platform;

import static kr.modusplant.shared.persistence.common.util.constant.FcmTokenConstant.*;


public interface FcmTokenEntityTestUtils extends SiteMemberEntityTestUtils {
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
