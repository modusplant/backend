package kr.modusplant.domains.member.common.util.app.http.response;

import kr.modusplant.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;

public interface SiteMemberResponseTestUtils extends SiteMemberTestUtils {
    SiteMemberResponse memberBasicUserResponse = new SiteMemberResponse(memberBasicUserWithUuid.getUuid(), memberBasicUser.getNickname(), memberBasicUser.getBirthDate(), memberBasicUser.getIsActive());

    SiteMemberResponse memberGoogleUserResponse = new SiteMemberResponse(memberGoogleUserWithUuid.getUuid(), memberGoogleUser.getNickname(), memberGoogleUser.getBirthDate(), memberGoogleUser.getIsActive());

    SiteMemberResponse memberKakaoUserResponse = new SiteMemberResponse(memberKakaoUserWithUuid.getUuid(), memberKakaoUser.getNickname(), memberKakaoUser.getBirthDate(), memberKakaoUser.getIsActive());
}
