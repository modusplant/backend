package kr.modusplant.domains.account.social.common.util.framework.out.jpa.entity;

import kr.modusplant.framework.jpa.entity.SiteMemberEntity;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.*;

public interface MemberEntityTestUtils {
    default SiteMemberEntity createKakaoMemberEntity() {
        return SiteMemberEntity.builder()
                .nickname(MEMBER_KAKAO_USER_NICKNAME)
                .birthDate(MEMBER_KAKAO_USER_BIRTH_DATE)
                .isActive(MEMBER_KAKAO_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_KAKAO_USER_LOGGED_IN_AT)
                .build();
    }

    default SiteMemberEntity createKakaoMemberEntityWithUuid() {
        return SiteMemberEntity.builder()
                .uuid(MEMBER_KAKAO_USER_UUID)
                .nickname(MEMBER_KAKAO_USER_NICKNAME)
                .birthDate(MEMBER_KAKAO_USER_BIRTH_DATE)
                .isActive(MEMBER_KAKAO_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_KAKAO_USER_LOGGED_IN_AT)
                .build();
    }

    default SiteMemberEntity createGoogleMemberEntity() {
        return SiteMemberEntity.builder()
                .nickname(MEMBER_GOOGLE_USER_NICKNAME)
                .birthDate(MEMBER_GOOGLE_USER_BIRTH_DATE)
                .isActive(MEMBER_GOOGLE_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_GOOGLE_USER_LOGGED_IN_AT)
                .build();
    }

    default SiteMemberEntity createGoogleMemberEntityWithUuid() {
        return SiteMemberEntity.builder()
                .uuid(MEMBER_GOOGLE_USER_UUID)
                .nickname(MEMBER_GOOGLE_USER_NICKNAME)
                .birthDate(MEMBER_GOOGLE_USER_BIRTH_DATE)
                .isActive(MEMBER_GOOGLE_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_GOOGLE_USER_LOGGED_IN_AT)
                .build();
    }


}
