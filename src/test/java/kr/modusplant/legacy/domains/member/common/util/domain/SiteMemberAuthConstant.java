package kr.modusplant.legacy.domains.member.common.util.domain;

import kr.modusplant.legacy.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

public interface SiteMemberAuthConstant {
    UUID MEMBER_AUTH_BASIC_ADMIN_UUID = UUID.fromString("48c75e56-34fb-4fc2-8e45-ee5669f79fdd");
    String MEMBER_AUTH_BASIC_ADMIN_EMAIL = "testAdmin1@gmail.com";
    String MEMBER_AUTH_BASIC_ADMIN_PW = new BCryptPasswordEncoder().encode("testPw12@");
    AuthProvider MEMBER_AUTH_BASIC_ADMIN_PROVIDER = AuthProvider.BASIC;

    UUID MEMBER_AUTH_BASIC_USER_ACTIVE_MEMBER_UUID = UUID.fromString("d6b716f1-60f7-4c79-aeaf-37037101f126");
    UUID MEMBER_AUTH_BASIC_USER_ORIGINAL_MEMBER_UUID = MEMBER_AUTH_BASIC_USER_ACTIVE_MEMBER_UUID;
    String MEMBER_AUTH_BASIC_USER_EMAIL = "TestBasicUser2@naver.com";
    String MEMBER_AUTH_BASIC_USER_PW = new BCryptPasswordEncoder().encode("Test!Pw14@");
    AuthProvider MEMBER_AUTH_BASIC_USER_PROVIDER = AuthProvider.BASIC;
    String MEMBER_AUTH_BASIC_USER_PROVIDER_ID = "";

    SiteMemberAuth memberAuthGoogleUser = SiteMemberAuth.builder()
            .email("Test3gOogleUsser@gmail.com")
            .provider(AuthProvider.GOOGLE)
            .providerId("639796866968871286823")
            .build();

    SiteMemberAuth memberAuthGoogleUserWithUuid = SiteMemberAuth.builder()
            .activeMemberUuid(SiteMemberConstant.memberGoogleUserWithUuid.getUuid())
            .originalMemberUuid(SiteMemberConstant.memberGoogleUserWithUuid.getUuid())
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
            .activeMemberUuid(SiteMemberConstant.memberKakaoUserWithUuid.getUuid())
            .originalMemberUuid(SiteMemberConstant.memberKakaoUserWithUuid.getUuid())
            .email(memberAuthKakaoUser.getEmail())
            .provider(memberAuthKakaoUser.getProvider())
            .providerId(memberAuthKakaoUser.getProviderId())
            .build();
}