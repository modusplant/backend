package kr.modusplant.domains.identity.social.common.util.framework.out.jpa.entity;

import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberEntity;

import static kr.modusplant.shared.persistence.common.constant.SiteMemberConstant.*;
import static kr.modusplant.shared.persistence.common.constant.SiteMemberConstant.MEMBER_KAKAO_USER_LOGGED_IN_AT;

public interface MemberEntityTestUtils {
    default MemberEntity createKakaoMemberEntity() {
        return MemberEntity.builder()
                .nickname(MEMBER_KAKAO_USER_NICKNAME)
                .birthDate(MEMBER_KAKAO_USER_BIRTH_DATE)
                .isActive(MEMBER_KAKAO_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_KAKAO_USER_LOGGED_IN_AT)
                .build();
    }

    default MemberEntity createKakaoMemberEntityWithUuid() {
        return MemberEntity.builder()
                .uuid(MEMBER_KAKAO_USER_UUID)
                .nickname(MEMBER_KAKAO_USER_NICKNAME)
                .birthDate(MEMBER_KAKAO_USER_BIRTH_DATE)
                .isActive(MEMBER_KAKAO_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_KAKAO_USER_LOGGED_IN_AT)
                .build();
    }

    default MemberEntity createGoogleMemberEntity() {
        return MemberEntity.builder()
                .nickname(MEMBER_GOOGLE_USER_NICKNAME)
                .birthDate(MEMBER_GOOGLE_USER_BIRTH_DATE)
                .isActive(MEMBER_GOOGLE_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_GOOGLE_USER_LOGGED_IN_AT)
                .build();
    }

    default MemberEntity createGoogleMemberEntityWithUuid() {
        return MemberEntity.builder()
                .uuid(MEMBER_GOOGLE_USER_UUID)
                .nickname(MEMBER_GOOGLE_USER_NICKNAME)
                .birthDate(MEMBER_GOOGLE_USER_BIRTH_DATE)
                .isActive(MEMBER_GOOGLE_USER_IS_ACTIVE)
                .loggedInAt(MEMBER_GOOGLE_USER_LOGGED_IN_AT)
                .build();
    }


}
