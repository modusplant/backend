package kr.modusplant.legacy.domains.member.common.util.app.http.response;

import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberAuthResponse;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberAuthConstant;

public interface SiteMemberAuthResponseTestUtils extends SiteMemberAuthConstant {
    SiteMemberAuthResponse memberAuthBasicUserResponse = new SiteMemberAuthResponse(memberAuthBasicUserWithUuid.getOriginalMemberUuid(), memberAuthBasicUserWithUuid.getActiveMemberUuid(), memberAuthBasicUser.getEmail(), memberAuthBasicUser.getProvider());

    SiteMemberAuthResponse memberAuthGoogleUserResponse = new SiteMemberAuthResponse(memberAuthGoogleUserWithUuid.getOriginalMemberUuid(), memberAuthGoogleUserWithUuid.getActiveMemberUuid(), memberAuthGoogleUser.getEmail(), memberAuthGoogleUser.getProvider());

    SiteMemberAuthResponse memberAuthKakaoUserResponse = new SiteMemberAuthResponse(memberAuthKakaoUserWithUuid.getOriginalMemberUuid(), memberAuthKakaoUserWithUuid.getActiveMemberUuid(), memberAuthKakaoUser.getEmail(), memberAuthKakaoUser.getProvider());
}
