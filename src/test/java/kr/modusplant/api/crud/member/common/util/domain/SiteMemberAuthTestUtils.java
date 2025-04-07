package kr.modusplant.api.crud.member.common.util.domain;

import kr.modusplant.api.crud.member.domain.model.SiteMemberAuth;
import kr.modusplant.api.crud.member.enums.AuthProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static kr.modusplant.api.crud.member.common.util.domain.SiteMemberTestUtils.*;

public interface SiteMemberAuthTestUtils {
    SiteMemberAuth memberAuthBasicAdmin = SiteMemberAuth.builder()
            .email("testAdmin1@gmail.com")
            .pw(new BCryptPasswordEncoder().encode("testPw12@"))
            .provider(AuthProvider.BASIC)
            .build();

    SiteMemberAuth memberAuthBasicAdminWithUuid = SiteMemberAuth.builder()
            .uuid(UUID.fromString("ff4ca1cb-e518-47e4-8344-2fa81f28a031"))
            .activeMemberUuid(memberBasicAdminWithUuid.getUuid())
            .originalMemberUuid(memberBasicAdminWithUuid.getUuid())
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
            .uuid(UUID.fromString("913ee77a-cec5-4b3e-9e95-60a7dc33a721"))
            .activeMemberUuid(memberBasicUserWithUuid.getUuid())
            .originalMemberUuid(memberBasicUserWithUuid.getUuid())
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
            .uuid(UUID.fromString("69c9a086-4c78-47cb-b4d5-84f9922e9031"))
            .activeMemberUuid(memberGoogleUserWithUuid.getUuid())
            .originalMemberUuid(memberGoogleUserWithUuid.getUuid())
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
            .uuid(UUID.fromString("cd523717-70fd-4353-955e-28b802e1970d"))
            .activeMemberUuid(memberKakaoUserWithUuid.getUuid())
            .originalMemberUuid(memberKakaoUserWithUuid.getUuid())
            .email(memberAuthKakaoUser.getEmail())
            .provider(memberAuthKakaoUser.getProvider())
            .providerId(memberAuthKakaoUser.getProviderId())
            .build();
}