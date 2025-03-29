package kr.modusplant.support.util.entity;

import static kr.modusplant.global.persistence.entity.SiteMemberAuthEntity.SiteMemberAuthEntityBuilder;
import static kr.modusplant.global.persistence.entity.SiteMemberAuthEntity.builder;
import static kr.modusplant.support.util.domain.SiteMemberAuthTestUtils.*;

public interface SiteMemberAuthEntityTestUtils extends SiteMemberEntityTestUtils {

    default SiteMemberAuthEntityBuilder createMemberAuthBasicAdminEntityBuilder() {
        return builder()
                .email(memberAuthBasicAdmin.getEmail())
                .pw(memberAuthBasicAdmin.getPw())
                .provider(memberAuthBasicAdmin.getProvider());
    }

    default SiteMemberAuthEntityBuilder createMemberAuthBasicAdminEntityWithUuidBuilder() {
        return builder()
                .uuid(memberAuthBasicAdminWithUuid.getUuid())
                .email(memberAuthBasicAdminWithUuid.getEmail())
                .pw(memberAuthBasicAdminWithUuid.getPw())
                .provider(memberAuthBasicAdminWithUuid.getProvider());
    }

    default SiteMemberAuthEntityBuilder createMemberAuthBasicUserEntityBuilder() {
        return builder()
                .email(memberAuthBasicUser.getEmail())
                .pw(memberAuthBasicUser.getPw())
                .provider(memberAuthBasicUser.getProvider());
    }

    default SiteMemberAuthEntityBuilder createMemberAuthBasicUserEntityWithUuidBuilder() {
        return builder()
                .uuid(memberAuthBasicUserWithUuid.getUuid())
                .email(memberAuthBasicUserWithUuid.getEmail())
                .pw(memberAuthBasicUserWithUuid.getPw())
                .provider(memberAuthBasicUserWithUuid.getProvider());
    }

    default SiteMemberAuthEntityBuilder createMemberAuthGoogleUserEntityBuilder() {
        return builder()
                .email(memberAuthGoogleUser.getEmail())
                .provider(memberAuthGoogleUser.getProvider())
                .providerId(memberAuthGoogleUser.getProviderId());
    }

    default SiteMemberAuthEntityBuilder createMemberAuthGoogleUserEntityWithUuidBuilder() {
        return builder()
                .uuid(memberAuthGoogleUserWithUuid.getUuid())
                .email(memberAuthGoogleUserWithUuid.getEmail())
                .provider(memberAuthGoogleUserWithUuid.getProvider())
                .providerId(memberAuthGoogleUserWithUuid.getProviderId());
    }

    default SiteMemberAuthEntityBuilder createMemberAuthKakaoUserEntityBuilder() {
        return builder()
                .email(memberAuthKakaoUser.getEmail())
                .provider(memberAuthKakaoUser.getProvider())
                .providerId(memberAuthKakaoUser.getProviderId());
    }

    default SiteMemberAuthEntityBuilder createMemberAuthKakaoUserEntityWithUuidBuilder() {
        return builder()
                .uuid(memberAuthKakaoUserWithUuid.getUuid())
                .email(memberAuthKakaoUserWithUuid.getEmail())
                .provider(memberAuthKakaoUserWithUuid.getProvider())
                .providerId(memberAuthKakaoUserWithUuid.getProviderId());
    }
}