package kr.modusplant.support.util.entity;

import kr.modusplant.global.enums.AuthProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static kr.modusplant.global.persistence.entity.SiteMemberAuthEntity.SiteMemberAuthEntityBuilder;
import static kr.modusplant.global.persistence.entity.SiteMemberAuthEntity.builder;

public interface SiteMemberAuthEntityTestUtils extends SiteMemberEntityTestUtils {

    default SiteMemberAuthEntityBuilder createMemberAuthBasicAdminEntityBuilder() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return builder()
                .email("testAdmin1@gmail.com")
                .pw(encoder.encode("testPw12@"))
                .provider(AuthProvider.BASIC);
    }

    default SiteMemberAuthEntityBuilder createMemberAuthBasicUserEntityBuilder() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return builder()
                .email("TestBasicUser2@naver.com")
                .pw(encoder.encode("Test!Pw14@"))
                .provider(AuthProvider.BASIC);
    }

    default SiteMemberAuthEntityBuilder createMemberAuthGoogleUserEntityBuilder() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return builder()
                .email("Test3gOogleUsser@gmail.com")
                .pw(encoder.encode("testPw12@"))
                .provider(AuthProvider.GOOGLE);
    }

    default SiteMemberAuthEntityBuilder createMemberAuthKakaoUserEntityBuilder() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return builder()
                .email("test2KaKao4Uzer@kakao.com")
                .pw(encoder.encode("ttEst^*Password1"))
                .provider(AuthProvider.KAKAO);
    }
}