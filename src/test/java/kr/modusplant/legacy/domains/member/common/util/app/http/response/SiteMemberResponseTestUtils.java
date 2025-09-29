package kr.modusplant.legacy.domains.member.common.util.app.http.response;

import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberConstant;

public interface SiteMemberResponseTestUtils extends SiteMemberConstant {
    SiteMemberResponse memberBasicUserResponse = new SiteMemberResponse(memberBasicUserWithUuid.getUuid(), memberBasicUser.getNickname(), memberBasicUser.getBirthDate(), memberBasicUser.getIsActive());

    SiteMemberResponse memberGoogleUserResponse = new SiteMemberResponse(memberGoogleUserWithUuid.getUuid(), memberGoogleUser.getNickname(), memberGoogleUser.getBirthDate(), memberGoogleUser.getIsActive());

    SiteMemberResponse memberKakaoUserResponse = new SiteMemberResponse(memberKakaoUserWithUuid.getUuid(), memberKakaoUser.getNickname(), memberKakaoUser.getBirthDate(), memberKakaoUser.getIsActive());
}
