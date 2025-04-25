package kr.modusplant.domains.member.common.util.app.http.response;

import kr.modusplant.domains.member.app.http.response.SiteMemberAuthResponse;
import kr.modusplant.domains.member.common.util.domain.SiteMemberAuthTestUtils;

public interface SiteMemberAuthResponseTestUtils extends SiteMemberAuthTestUtils {
    SiteMemberAuthResponse memberAuthBasicUserResponse = new SiteMemberAuthResponse(memberAuthBasicUserWithUuid.getUuid(), memberAuthBasicUserWithUuid.getActiveMemberUuid(), memberAuthBasicUserWithUuid.getOriginalMemberUuid(), memberAuthBasicUser.getEmail(), memberAuthBasicUser.getProvider());

    SiteMemberAuthResponse memberAuthGoogleUserResponse = new SiteMemberAuthResponse(memberAuthGoogleUserWithUuid.getUuid(), memberAuthGoogleUserWithUuid.getActiveMemberUuid(), memberAuthGoogleUserWithUuid.getOriginalMemberUuid(), memberAuthGoogleUser.getEmail(), memberAuthGoogleUser.getProvider());

    SiteMemberAuthResponse memberAuthKakaoUserResponse = new SiteMemberAuthResponse(memberAuthKakaoUserWithUuid.getUuid(), memberAuthKakaoUserWithUuid.getActiveMemberUuid(), memberAuthKakaoUserWithUuid.getOriginalMemberUuid(), memberAuthKakaoUser.getEmail(), memberAuthKakaoUser.getProvider());
}
