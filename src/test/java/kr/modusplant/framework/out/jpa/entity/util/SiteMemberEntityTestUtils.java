package kr.modusplant.framework.out.jpa.entity.util;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.constant.SiteMemberEntityConstant;

public interface SiteMemberEntityTestUtils extends SiteMemberEntityConstant {
    default SiteMemberEntity createMemberBasicAdminEntity() {
        return SiteMemberEntity.builder()
                .nickname(MEMBER_BASIC_ADMIN_NICKNAME)
                .birthDate(MEMBER_BASIC_ADMIN_BIRTH_DATE)
                .isActive(MEMBER_BASIC_ADMIN_IS_ACTIVE)
                .loggedInAt(MEMBER_BASIC_ADMIN_LOGGED_IN_AT)
                .build();
    }

    default SiteMemberEntity createMemberBasicAdminEntityWithUuid() {
        return SiteMemberEntity.builder()
                .uuid(MEMBER_BASIC_ADMIN_UUID)
                .nickname(MEMBER_BASIC_ADMIN_NICKNAME)
                .birthDate(MEMBER_BASIC_ADMIN_BIRTH_DATE)
                .isActive(MEMBER_BASIC_ADMIN_IS_ACTIVE)
                .loggedInAt(MEMBER_BASIC_ADMIN_LOGGED_IN_AT)
                .build();
    }

    default SiteMemberEntity createMemberBasicUserEntity() {
        return SiteMemberEntity.builder()
                .nickname(MEMBER_BASIC_USER_NICKNAME)
                .birthDate(MEMBER_BASIC_USER_BIRTH_DATE)
                .isActive(MEMBER_BASIC_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_BASIC_USER_LOGGED_IN_AT)
                .build();
    }

    default SiteMemberEntity createMemberBasicUserEntityWithUuid() {
        return SiteMemberEntity.builder()
                .uuid(MEMBER_BASIC_USER_UUID)
                .nickname(MEMBER_BASIC_USER_NICKNAME)
                .birthDate(MEMBER_BASIC_USER_BIRTH_DATE)
                .isActive(MEMBER_BASIC_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_BASIC_USER_LOGGED_IN_AT)
                .build();
    }

    default SiteMemberEntity createMemberGoogleUserEntity() {
        return SiteMemberEntity.builder()
                .nickname(MEMBER_GOOGLE_USER_NICKNAME)
                .birthDate(MEMBER_GOOGLE_USER_BIRTH_DATE)
                .isActive(MEMBER_GOOGLE_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_GOOGLE_USER_LOGGED_IN_AT)
                .build();
    }

    default SiteMemberEntity createMemberGoogleUserEntityWithUuid() {
        return SiteMemberEntity.builder()
                .uuid(MEMBER_GOOGLE_USER_UUID)
                .nickname(MEMBER_GOOGLE_USER_NICKNAME)
                .birthDate(MEMBER_GOOGLE_USER_BIRTH_DATE)
                .isActive(MEMBER_GOOGLE_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_GOOGLE_USER_LOGGED_IN_AT)
                .build();
    }

    default SiteMemberEntity createMemberKakaoUserEntity() {
        return SiteMemberEntity.builder()
                .nickname(MEMBER_KAKAO_USER_NICKNAME)
                .birthDate(MEMBER_KAKAO_USER_BIRTH_DATE)
                .isActive(MEMBER_KAKAO_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_KAKAO_USER_LOGGED_IN_AT)
                .build();
    }

    default SiteMemberEntity createMemberKakaoUserEntityWithUuid() {
        return SiteMemberEntity.builder()
                .uuid(MEMBER_KAKAO_USER_UUID)
                .nickname(MEMBER_KAKAO_USER_NICKNAME)
                .birthDate(MEMBER_KAKAO_USER_BIRTH_DATE)
                .isActive(MEMBER_KAKAO_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_KAKAO_USER_LOGGED_IN_AT)
                .build();
    }
}
