package kr.modusplant.support.util.entity;

import kr.modusplant.global.enums.AuthProvider;
import kr.modusplant.global.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public interface SiteMemberAuthEntityTestUtils extends SiteMemberEntityTestUtils {

    default SiteMemberAuthEntity createMemberAuthBasicAdminEntity() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        SiteMemberEntity memberUser = createMemberBasicAdminEntity();
        return SiteMemberAuthEntity.builder()
                .activeMember(memberUser)
                .originalMember(memberUser)
                .email("testAdmin1@gmail.com")
                .pw(encoder.encode("testPw12@"))
                .provider(AuthProvider.BASIC)
                .build();
    }

    default SiteMemberAuthEntity createMemberAuthBasicUserEntity() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        SiteMemberEntity memberUser = createMemberBasicUserEntity();
        return SiteMemberAuthEntity.builder()
                .activeMember(memberUser)
                .originalMember(memberUser)
                .email("TestBasicUser2@naver.com")
                .pw(encoder.encode("Test!Pw14@"))
                .provider(AuthProvider.BASIC)
                .build();
    }

    default SiteMemberAuthEntity createMemberAuthGoogleUserEntity() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        SiteMemberEntity memberUser = createMemberGoogleUserEntity();
        return SiteMemberAuthEntity.builder()
                .activeMember(memberUser)
                .originalMember(memberUser)
                .email("Test3gOogleUsser@gmail.com")
                .pw(encoder.encode("testPw12@"))
                .provider(AuthProvider.GOOGLE)
                .build();
    }

    default SiteMemberAuthEntity createMemberAuthKakaoUserEntity() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        SiteMemberEntity memberUser = createMemberKakaoUserEntity();
        return SiteMemberAuthEntity.builder()
                .activeMember(memberUser)
                .originalMember(memberUser)
                .email("test2KaKao4Uzer@kakao.com")
                .pw(encoder.encode("ttEst^*Password1"))
                .provider(AuthProvider.KAKAO)
                .build();
    }
}