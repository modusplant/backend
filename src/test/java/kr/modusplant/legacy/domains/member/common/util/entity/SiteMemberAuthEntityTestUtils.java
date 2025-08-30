package kr.modusplant.legacy.domains.member.common.util.entity;

import static kr.modusplant.framework.out.persistence.jpa.entity.SiteMemberAuthEntity.SiteMemberAuthEntityBuilder;
import static kr.modusplant.framework.out.persistence.jpa.entity.SiteMemberAuthEntity.builder;
import static kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberAuthTestUtils.*;

public interface SiteMemberAuthEntityTestUtils extends SiteMemberEntityTestUtils {

    default SiteMemberAuthEntityBuilder createMemberAuthBasicAdminEntityBuilder() {
        return builder()
                .email(memberAuthBasicAdmin.getEmail())
                .pw(memberAuthBasicAdmin.getPw())
                .provider(memberAuthBasicAdmin.getProvider());
    }

    default SiteMemberAuthEntityBuilder createMemberAuthBasicUserEntityBuilder() {
        return builder()
                .email(memberAuthBasicUser.getEmail())
                .pw(memberAuthBasicUser.getPw())
                .provider(memberAuthBasicUser.getProvider());
    }

    default SiteMemberAuthEntityBuilder createMemberAuthGoogleUserEntityBuilder() {
        return builder()
                .email(memberAuthGoogleUser.getEmail())
                .provider(memberAuthGoogleUser.getProvider())
                .providerId(memberAuthGoogleUser.getProviderId());
    }

    default SiteMemberAuthEntityBuilder createMemberAuthKakaoUserEntityBuilder() {
        return builder()
                .email(memberAuthKakaoUser.getEmail())
                .provider(memberAuthKakaoUser.getProvider())
                .providerId(memberAuthKakaoUser.getProviderId());
    }
}