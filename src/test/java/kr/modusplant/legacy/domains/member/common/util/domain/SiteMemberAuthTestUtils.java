package kr.modusplant.legacy.domains.member.common.util.domain;

import kr.modusplant.legacy.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public interface SiteMemberAuthTestUtils {
    SiteMemberAuth memberAuthBasicAdmin = SiteMemberAuth.builder()
            .email("testAdmin1@gmail.com")
            .pw(new BCryptPasswordEncoder().encode("testPw12@"))
            .provider(AuthProvider.BASIC)
            .build();

    SiteMemberAuth memberAuthBasicAdminWithUuid = SiteMemberAuth.builder()
            .activeMemberUuid(SiteMemberTestUtils.memberBasicAdminWithUuid.getUuid())
            .originalMemberUuid(SiteMemberTestUtils.memberBasicAdminWithUuid.getUuid())
            .email(memberAuthBasicAdmin.getEmail())
            .pw(memberAuthBasicAdmin.getPw())
            .provider(memberAuthBasicAdmin.getProvider())
            .build();

    SiteMemberAuth memberAuthBasicUser = SiteMemberAuth.builder()
            .email("TestBasicUser2@naver.com")
            .pw(new BCryptPasswordEncoder().encode("Test!Pw14@"))
            .provider(AuthProvider.BASIC)
            .build();

    SiteMemberAuth memberAuthBasicUserWithUuid = SiteMemberAuth.builder()
            .activeMemberUuid(SiteMemberTestUtils.memberBasicUserWithUuid.getUuid())
            .originalMemberUuid(SiteMemberTestUtils.memberBasicUserWithUuid.getUuid())
            .email(memberAuthBasicUser.getEmail())
            .pw(memberAuthBasicUser.getPw())
            .provider(memberAuthBasicUser.getProvider())
            .build();

    SiteMemberAuth memberAuthGoogleUser = SiteMemberAuth.builder()
            .email("Test3gOogleUsser@gmail.com")
            .provider(AuthProvider.GOOGLE)
            .providerId("639796866968871286823")
            .build();

    SiteMemberAuth memberAuthGoogleUserWithUuid = SiteMemberAuth.builder()
            .activeMemberUuid(SiteMemberTestUtils.memberGoogleUserWithUuid.getUuid())
            .originalMemberUuid(SiteMemberTestUtils.memberGoogleUserWithUuid.getUuid())
            .email(memberAuthGoogleUser.getEmail())
            .provider(memberAuthGoogleUser.getProvider())
            .providerId(memberAuthGoogleUser.getProviderId())
            .build();

    SiteMemberAuth memberAuthKakaoUser = SiteMemberAuth.builder()
            .email("test2KaKao4Uzer@kakao.com")
            .provider(AuthProvider.KAKAO)
            .providerId("9348634889")
            .build();

    SiteMemberAuth memberAuthKakaoUserWithUuid = SiteMemberAuth.builder()
            .activeMemberUuid(SiteMemberTestUtils.memberKakaoUserWithUuid.getUuid())
            .originalMemberUuid(SiteMemberTestUtils.memberKakaoUserWithUuid.getUuid())
            .email(memberAuthKakaoUser.getEmail())
            .provider(memberAuthKakaoUser.getProvider())
            .providerId(memberAuthKakaoUser.getProviderId())
            .build();
}