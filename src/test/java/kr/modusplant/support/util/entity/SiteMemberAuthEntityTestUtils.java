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

    default SiteMemberAuthEntityBuilder createMemberAuthBasicUserEntityBuilder() {
        return builder()
                .email(memberAuthBasicUser.getEmail())
                .pw(memberAuthBasicUser.getPw())
                .provider(memberAuthBasicUser.getProvider());
    }

    default SiteMemberAuthEntityBuilder createMemberAuthGoogleUserEntityBuilder() {
        return builder()
                .email(memberAuthGoogleUser.getEmail())
                .pw(memberAuthGoogleUser.getPw())
                .provider(memberAuthGoogleUser.getProvider());
    }

    default SiteMemberAuthEntityBuilder createMemberAuthKakaoUserEntityBuilder() {
        return builder()
                .email(memberAuthKakaoUser.getEmail())
                .pw(memberAuthKakaoUser.getPw())
                .provider(memberAuthKakaoUser.getProvider());
    }
}