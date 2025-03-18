package kr.modusplant.support.util.entity;

import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.support.util.domain.SiteMemberTestUtils;

public interface SiteMemberEntityTestUtils extends SiteMemberTestUtils {
    default SiteMemberEntity createMemberBasicAdminEntity() {
        return SiteMemberEntity.builder()
                .nickname(memberBasicAdmin.getNickname())
                .birthDate(memberBasicAdmin.getBirthDate())
                .loggedInAt(memberBasicAdmin.getLoggedInAt())
                .build();
    }

    default SiteMemberEntity createMemberBasicUserEntity() {
        return SiteMemberEntity.builder()
                .nickname(memberBasicUser.getNickname())
                .birthDate(memberBasicUser.getBirthDate())
                .loggedInAt(memberBasicUser.getLoggedInAt())
                .build();
    }

    default SiteMemberEntity createMemberGoogleUserEntity() {
        return SiteMemberEntity.builder()
                .nickname(memberGoogleUser.getNickname())
                .birthDate(memberGoogleUser.getBirthDate())
                .loggedInAt(memberGoogleUser.getLoggedInAt())
                .build();
    }

    default SiteMemberEntity createMemberKakaoUserEntity() {
        return SiteMemberEntity.builder()
                .nickname(memberKakaoUser.getNickname())
                .birthDate(memberKakaoUser.getBirthDate())
                .loggedInAt(memberKakaoUser.getLoggedInAt())
                .build();
    }
}
