package kr.modusplant.legacy.domains.member.common.util.entity;

import static kr.modusplant.framework.out.jpa.entity.SiteMemberAuthEntity.SiteMemberAuthEntityBuilder;
import static kr.modusplant.framework.out.jpa.entity.SiteMemberAuthEntity.builder;
import static kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberAuthConstant.*;

public interface SiteMemberAuthEntityConstant extends SiteMemberEntityConstant {

    default SiteMemberAuthEntityBuilder createMemberAuthBasicAdminEntityBuilder() {
        return builder()
                .email(MEMBER_AUTH_BASIC_ADMIN_EMAIL)
                .pw(MEMBER_AUTH_BASIC_ADMIN_PW)
                .provider(MEMBER_AUTH_BASIC_ADMIN_PROVIDER);
    }

    default SiteMemberAuthEntityBuilder createMemberAuthBasicUserEntityBuilder() {
        return builder()
                .email(MEMBER_AUTH_BASIC_USER_EMAIL)
                .pw(MEMBER_AUTH_BASIC_USER_PW)
                .provider(MEMBER_AUTH_BASIC_USER_PROVIDER);
    }

    default SiteMemberAuthEntityBuilder createMemberAuthGoogleUserEntityBuilder() {
        return builder()
                .email(MEMBER_AUTH_GOOGLE_USER_EMAIL)
                .provider(MEMBER_AUTH_GOOGLE_USER_PROVIDER)
                .providerId(MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID);
    }

    default SiteMemberAuthEntityBuilder createMemberAuthKakaoUserEntityBuilder() {
        return builder()
                .email(MEMBER_AUTH_KAKAO_USER_EMAIL)
                .provider(MEMBER_AUTH_KAKAO_USER_PROVIDER)
                .providerId(MEMBER_AUTH_KAKAO_USER_PROVIDER_ID);
    }
}