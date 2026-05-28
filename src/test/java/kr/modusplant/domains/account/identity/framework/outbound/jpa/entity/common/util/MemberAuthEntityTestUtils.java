package kr.modusplant.domains.account.identity.framework.outbound.jpa.entity.common.util;

import kr.modusplant.domains.account.identity.framework.outbound.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.MemberEntityTestUtils;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.*;
import static kr.modusplant.domains.account.identity.framework.outbound.jpa.entity.MemberAuthEntity.MemberAuthEntityBuilder;

public interface MemberAuthEntityTestUtils extends MemberEntityTestUtils {
    default MemberAuthEntityBuilder createMemberAuthBasicAdminEntityBuilder() {
        return MemberAuthEntity.builder()
                .email(MEMBER_AUTH_BASIC_ADMIN_EMAIL)
                .pw(MEMBER_AUTH_BASIC_ADMIN_PW)
                .provider(MEMBER_AUTH_BASIC_ADMIN_PROVIDER);
    }

    default MemberAuthEntityBuilder createMemberAuthBasicUserEntityBuilder() {
        return MemberAuthEntity.builder()
                .email(MEMBER_AUTH_BASIC_USER_EMAIL)
                .pw(MEMBER_AUTH_BASIC_USER_PW)
                .provider(MEMBER_AUTH_BASIC_USER_PROVIDER);
    }

    default MemberAuthEntityBuilder createMemberAuthGoogleUserEntityBuilder() {
        return MemberAuthEntity.builder()
                .email(MEMBER_AUTH_GOOGLE_USER_EMAIL)
                .provider(MEMBER_AUTH_GOOGLE_USER_PROVIDER)
                .providerId(MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID);
    }

    default MemberAuthEntityBuilder createMemberAuthKakaoUserEntityBuilder() {
        return MemberAuthEntity.builder()
                .email(MEMBER_AUTH_KAKAO_USER_EMAIL)
                .provider(MEMBER_AUTH_KAKAO_USER_PROVIDER)
                .providerId(MEMBER_AUTH_KAKAO_USER_PROVIDER_ID);
    }

    default MemberAuthEntityBuilder createMemberAuthBasicGoogleEntityBuilder() {
        return MemberAuthEntity.builder()
                .email(MEMBER_AUTH_BASIC_USER_EMAIL)
                .pw(MEMBER_AUTH_BASIC_USER_PW)
                .provider(MEMBER_AUTH_BASIC_GOOGLE_USER_PROVIDER)
                .providerId(MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID);
    }

    default MemberAuthEntityBuilder createMemberAuthBasicKakaoEntityBuilder() {
        return MemberAuthEntity.builder()
                .email(MEMBER_AUTH_BASIC_USER_EMAIL)
                .pw(MEMBER_AUTH_BASIC_USER_PW)
                .provider(MEMBER_AUTH_BASIC_KAKAO_USER_PROVIDER)
                .providerId(MEMBER_AUTH_KAKAO_USER_PROVIDER_ID);
    }
}