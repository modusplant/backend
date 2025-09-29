package kr.modusplant.legacy.domains.member.common.util.app.http.request;

import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberUpdateRequest;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberConstant;

public interface SiteMemberRequestTestUtils extends SiteMemberConstant {
    SiteMemberInsertRequest memberBasicUserInsertRequest = new SiteMemberInsertRequest(MEMBER_BASIC_USER_NICKNAME);

    SiteMemberUpdateRequest memberBasicUserUpdateRequest = new SiteMemberUpdateRequest(MEMBER_BASIC_USER_UUID, MEMBER_BASIC_USER_NICKNAME, MEMBER_BASIC_USER_BIRTH_DATE);

    SiteMemberInsertRequest memberGoogleUserInsertRequest = new SiteMemberInsertRequest(MEMBER_GOOGLE_USER_NICKNAME);

    SiteMemberUpdateRequest memberGoogleUserUpdateRequest = new SiteMemberUpdateRequest(MEMBER_GOOGLE_USER_UUID, MEMBER_GOOGLE_USER_NICKNAME, MEMBER_GOOGLE_USER_BIRTH_DATE);

    SiteMemberInsertRequest memberKakaoUserInsertRequest = new SiteMemberInsertRequest(MEMBER_KAKAO_USER_NICKNAME);

    SiteMemberUpdateRequest memberKakaoUserUpdateRequest = new SiteMemberUpdateRequest(MEMBER_KAKAO_USER_UUID, MEMBER_KAKAO_USER_NICKNAME, MEMBER_KAKAO_USER_BIRTH_DATE);
}
