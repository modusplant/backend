package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;

import static kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity.SiteMemberAuthEntityBuilder;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.*;

public interface SiteMemberAuthEntityTestUtils extends SiteMemberEntityTestUtils {
    default SiteMemberAuthEntityBuilder createMemberAuthBasicAdminEntityBuilder() {
        return SiteMemberAuthEntity.builder()
                .email(MEMBER_AUTH_BASIC_ADMIN_EMAIL)
                .pw(MEMBER_AUTH_BASIC_ADMIN_PW)
                .provider(MEMBER_AUTH_BASIC_ADMIN_PROVIDER);
    }

    default SiteMemberAuthEntityBuilder createMemberAuthBasicUserEntityBuilder() {
        return SiteMemberAuthEntity.builder()
                .email(MEMBER_AUTH_BASIC_USER_EMAIL)
                .pw(MEMBER_AUTH_BASIC_USER_PW)
                .provider(MEMBER_AUTH_BASIC_USER_PROVIDER);
    }

    default SiteMemberAuthEntityBuilder createMemberAuthGoogleUserEntityBuilder() {
        return SiteMemberAuthEntity.builder()
                .email(MEMBER_AUTH_GOOGLE_USER_EMAIL)
                .provider(MEMBER_AUTH_GOOGLE_USER_PROVIDER)
                .providerId(MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID);
    }

    default SiteMemberAuthEntityBuilder createMemberAuthKakaoUserEntityBuilder() {
        return SiteMemberAuthEntity.builder()
                .email(MEMBER_AUTH_KAKAO_USER_EMAIL)
                .provider(MEMBER_AUTH_KAKAO_USER_PROVIDER)
                .providerId(MEMBER_AUTH_KAKAO_USER_PROVIDER_ID);
    }
}