<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/member/common/util/entity/SiteMemberEntityTestUtils.java
package kr.modusplant.domains.member.common.util.entity;

import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
========
package kr.modusplant.api.crud.member.common.util.entity;

import kr.modusplant.api.crud.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/test/java/kr/modusplant/api/crud/member/common/util/entity/SiteMemberEntityTestUtils.java

public interface SiteMemberEntityTestUtils extends SiteMemberTestUtils {
    default SiteMemberEntity createMemberBasicAdminEntity() {
        return SiteMemberEntity.builder()
                .nickname(memberBasicAdmin.getNickname())
                .birthDate(memberBasicAdmin.getBirthDate())
                .loggedInAt(memberBasicAdmin.getLoggedInAt())
                .build();
    }

    default SiteMemberEntity createMemberBasicAdminEntityWithUuid() {
        return SiteMemberEntity.builder()
                .uuid(memberBasicAdminWithUuid.getUuid())
                .nickname(memberBasicAdminWithUuid.getNickname())
                .birthDate(memberBasicAdminWithUuid.getBirthDate())
                .loggedInAt(memberBasicAdminWithUuid.getLoggedInAt())
                .build();
    }

    default SiteMemberEntity createMemberBasicUserEntity() {
        return SiteMemberEntity.builder()
                .nickname(memberBasicUser.getNickname())
                .birthDate(memberBasicUser.getBirthDate())
                .loggedInAt(memberBasicUser.getLoggedInAt())
                .build();
    }

    default SiteMemberEntity createMemberBasicUserEntityWithUuid() {
        return SiteMemberEntity.builder()
                .uuid(memberBasicUserWithUuid.getUuid())
                .nickname(memberBasicUserWithUuid.getNickname())
                .birthDate(memberBasicUserWithUuid.getBirthDate())
                .loggedInAt(memberBasicUserWithUuid.getLoggedInAt())
                .build();
    }

    default SiteMemberEntity createMemberGoogleUserEntity() {
        return SiteMemberEntity.builder()
                .nickname(memberGoogleUser.getNickname())
                .birthDate(memberGoogleUser.getBirthDate())
                .loggedInAt(memberGoogleUser.getLoggedInAt())
                .build();
    }

    default SiteMemberEntity createMemberGoogleUserEntityWithUuid() {
        return SiteMemberEntity.builder()
                .uuid(memberGoogleUserWithUuid.getUuid())
                .nickname(memberGoogleUserWithUuid.getNickname())
                .birthDate(memberGoogleUserWithUuid.getBirthDate())
                .loggedInAt(memberGoogleUserWithUuid.getLoggedInAt())
                .build();
    }

    default SiteMemberEntity createMemberKakaoUserEntity() {
        return SiteMemberEntity.builder()
                .nickname(memberKakaoUser.getNickname())
                .birthDate(memberKakaoUser.getBirthDate())
                .loggedInAt(memberKakaoUser.getLoggedInAt())
                .build();
    }

    default SiteMemberEntity createMemberKakaoUserEntityWithUuid() {
        return SiteMemberEntity.builder()
                .uuid(memberKakaoUserWithUuid.getUuid())
                .nickname(memberKakaoUserWithUuid.getNickname())
                .birthDate(memberKakaoUserWithUuid.getBirthDate())
                .loggedInAt(memberKakaoUserWithUuid.getLoggedInAt())
                .build();
    }
}
