package kr.modusplant.support.util.domain;

import kr.modusplant.global.domain.model.SiteMemberAuth;
import kr.modusplant.global.enums.AuthProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public interface SiteMemberAuthTestUtils {
    SiteMemberAuth memberAuthBasicAdmin = SiteMemberAuth.builder()
            .email("testAdmin1@gmail.com")
            .pw(new BCryptPasswordEncoder().encode("testPw12@"))
            .provider(AuthProvider.BASIC)
            .build();

    SiteMemberAuth memberAuthBasicUser = SiteMemberAuth.builder()
            .email("TestBasicUser2@naver.com")
            .pw(new BCryptPasswordEncoder().encode("Test!Pw14@"))
            .provider(AuthProvider.BASIC)
            .build();

    SiteMemberAuth memberAuthGoogleUser = SiteMemberAuth.builder()
            .email("Test3gOogleUsser@gmail.com")
            .pw(new BCryptPasswordEncoder().encode("testPw12@"))
            .provider(AuthProvider.GOOGLE)
            .providerId("639796866968871286823")
            .build();

    SiteMemberAuth memberAuthKakaoUser = SiteMemberAuth.builder()
            .email("test2KaKao4Uzer@kakao.com")
            .pw(new BCryptPasswordEncoder().encode("ttEst^*Password1"))
            .provider(AuthProvider.KAKAO)
            .providerId("9348634889")
            .build();
}