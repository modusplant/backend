<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/member/common/util/domain/SiteMemberAuthTestUtils.java
package kr.modusplant.domains.member.common.util.domain;

import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.enums.AuthProvider;
========
package kr.modusplant.api.crud.member.common.util.domain;

import kr.modusplant.api.crud.member.domain.model.SiteMemberAuth;
import kr.modusplant.api.crud.member.enums.AuthProvider;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/test/java/kr/modusplant/api/crud/member/common/util/domain/SiteMemberAuthTestUtils.java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/member/common/util/domain/SiteMemberAuthTestUtils.java
========
import static kr.modusplant.api.crud.member.common.util.domain.SiteMemberTestUtils.*;

>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/test/java/kr/modusplant/api/crud/member/common/util/domain/SiteMemberAuthTestUtils.java
public interface SiteMemberAuthTestUtils {
    SiteMemberAuth memberAuthBasicAdmin = SiteMemberAuth.builder()
            .email("testAdmin1@gmail.com")
            .pw(new BCryptPasswordEncoder().encode("testPw12@"))
            .provider(AuthProvider.BASIC)
            .build();

    SiteMemberAuth memberAuthBasicAdminWithUuid = SiteMemberAuth.builder()
            .uuid(UUID.fromString("ff4ca1cb-e518-47e4-8344-2fa81f28a031"))
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
            .uuid(UUID.fromString("913ee77a-cec5-4b3e-9e95-60a7dc33a721"))
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
            .uuid(UUID.fromString("69c9a086-4c78-47cb-b4d5-84f9922e9031"))
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
            .uuid(UUID.fromString("cd523717-70fd-4353-955e-28b802e1970d"))
            .activeMemberUuid(SiteMemberTestUtils.memberKakaoUserWithUuid.getUuid())
            .originalMemberUuid(SiteMemberTestUtils.memberKakaoUserWithUuid.getUuid())
            .email(memberAuthKakaoUser.getEmail())
            .provider(memberAuthKakaoUser.getProvider())
            .providerId(memberAuthKakaoUser.getProviderId())
            .build();
}