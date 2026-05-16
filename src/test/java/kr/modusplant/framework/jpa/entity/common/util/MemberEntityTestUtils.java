package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.MemberEntity;

import static kr.modusplant.shared.persistence.common.util.constant.MemberConstant.*;

public interface MemberEntityTestUtils {
    default MemberEntity createMemberBasicAdminEntity() {
        return MemberEntity.builder()
                .nickname(MEMBER_BASIC_ADMIN_NICKNAME)
                .role(MEMBER_BASIC_ADMIN_ROLE)
                .isActive(MEMBER_BASIC_ADMIN_IS_ACTIVE)
                .loggedInAt(MEMBER_BASIC_ADMIN_LOGGED_IN_AT)
                .build();
    }

    default MemberEntity createMemberBasicAdminEntityWithUuid() {
        return MemberEntity.builder()
                .uuid(MEMBER_BASIC_ADMIN_UUID)
                .nickname(MEMBER_BASIC_ADMIN_NICKNAME)
                .role(MEMBER_BASIC_ADMIN_ROLE)
                .isActive(MEMBER_BASIC_ADMIN_IS_ACTIVE)
                .loggedInAt(MEMBER_BASIC_ADMIN_LOGGED_IN_AT)
                .build();
    }

    default MemberEntity createMemberBasicUserEntity() {
        return MemberEntity.builder()
                .nickname(MEMBER_BASIC_USER_NICKNAME)
                .role(MEMBER_BASIC_USER_ROLE)
                .isActive(MEMBER_BASIC_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_BASIC_USER_LOGGED_IN_AT)
                .build();
    }

    default MemberEntity createMemberBasicUserEntityWithUuid() {
        return MemberEntity.builder()
                .uuid(MEMBER_BASIC_USER_UUID)
                .nickname(MEMBER_BASIC_USER_NICKNAME)
                .role(MEMBER_BASIC_USER_ROLE)
                .isActive(MEMBER_BASIC_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_BASIC_USER_LOGGED_IN_AT)
                .build();
    }

    default MemberEntity createMemberGoogleUserEntity() {
        return MemberEntity.builder()
                .nickname(MEMBER_GOOGLE_USER_NICKNAME)
                .role(MEMBER_GOOGLE_USER_ROLE)
                .isActive(MEMBER_GOOGLE_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_GOOGLE_USER_LOGGED_IN_AT)
                .build();
    }

    default MemberEntity createMemberGoogleUserEntityWithUuid() {
        return MemberEntity.builder()
                .uuid(MEMBER_GOOGLE_USER_UUID)
                .nickname(MEMBER_GOOGLE_USER_NICKNAME)
                .role(MEMBER_GOOGLE_USER_ROLE)
                .isActive(MEMBER_GOOGLE_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_GOOGLE_USER_LOGGED_IN_AT)
                .build();
    }

    default MemberEntity createMemberKakaoUserEntity() {
        return MemberEntity.builder()
                .nickname(MEMBER_KAKAO_USER_NICKNAME)
                .role(MEMBER_KAKAO_USER_ROLE)
                .isActive(MEMBER_KAKAO_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_KAKAO_USER_LOGGED_IN_AT)
                .build();
    }

    default MemberEntity createMemberKakaoUserEntityWithUuid() {
        return MemberEntity.builder()
                .uuid(MEMBER_KAKAO_USER_UUID)
                .nickname(MEMBER_KAKAO_USER_NICKNAME)
                .role(MEMBER_KAKAO_USER_ROLE)
                .isActive(MEMBER_KAKAO_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_KAKAO_USER_LOGGED_IN_AT)
                .build();
    }
}
