package kr.modusplant.legacy.domains.member.common.util.app.http.response;

import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberResponse;

import static kr.modusplant.framework.out.jpa.entity.common.constant.SiteMemberEntityConstant.*;

public interface SiteMemberResponseTestUtils {
    SiteMemberResponse memberBasicUserResponse = new SiteMemberResponse(MEMBER_BASIC_USER_UUID, MEMBER_BASIC_USER_NICKNAME, MEMBER_BASIC_USER_BIRTH_DATE, MEMBER_BASIC_USER_IS_ACTIVE);

    SiteMemberResponse memberGoogleUserResponse = new SiteMemberResponse(MEMBER_GOOGLE_USER_UUID, MEMBER_GOOGLE_USER_NICKNAME, MEMBER_GOOGLE_USER_BIRTH_DATE, MEMBER_GOOGLE_USER_IS_ACTIVE);

    SiteMemberResponse memberKakaoUserResponse = new SiteMemberResponse(MEMBER_KAKAO_USER_UUID, MEMBER_KAKAO_USER_NICKNAME, MEMBER_KAKAO_USER_BIRTH_DATE, MEMBER_KAKAO_USER_IS_ACTIVE);
}
