package kr.modusplant.legacy.domains.member.common.util.app.http.request;

import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberAuthInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberAuthUpdateRequest;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberAuthConstant;

public interface SiteMemberAuthRequestTestUtils extends SiteMemberAuthConstant {
    SiteMemberAuthInsertRequest memberAuthBasicUserInsertRequest = new SiteMemberAuthInsertRequest(MEMBER_AUTH_BASIC_USER_ORIGINAL_MEMBER_UUID, MEMBER_AUTH_BASIC_USER_EMAIL, MEMBER_AUTH_BASIC_USER_PW, MEMBER_AUTH_BASIC_USER_PROVIDER, MEMBER_AUTH_BASIC_USER_PROVIDER_ID);

    SiteMemberAuthUpdateRequest memberAuthBasicUserUpdateRequest = new SiteMemberAuthUpdateRequest(MEMBER_AUTH_BASIC_USER_ORIGINAL_MEMBER_UUID, MEMBER_AUTH_BASIC_USER_EMAIL, MEMBER_AUTH_BASIC_USER_PW);

    SiteMemberAuthInsertRequest memberAuthGoogleUserInsertRequest = new SiteMemberAuthInsertRequest(memberAuthGoogleUserWithUuid.getOriginalMemberUuid(), memberAuthGoogleUser.getEmail(), memberAuthGoogleUser.getPw(), memberAuthGoogleUser.getProvider(), memberAuthGoogleUser.getProviderId());

    SiteMemberAuthUpdateRequest memberAuthGoogleUserUpdateRequest = new SiteMemberAuthUpdateRequest(memberAuthGoogleUserWithUuid.getOriginalMemberUuid(), memberAuthGoogleUser.getEmail(), memberAuthGoogleUser.getPw());

    SiteMemberAuthInsertRequest memberAuthKakaoUserInsertRequest = new SiteMemberAuthInsertRequest(memberAuthKakaoUserWithUuid.getOriginalMemberUuid(), memberAuthKakaoUser.getEmail(), memberAuthKakaoUser.getPw(), memberAuthKakaoUser.getProvider(), memberAuthKakaoUser.getProviderId());

    SiteMemberAuthUpdateRequest memberAuthKakaoUserUpdateRequest = new SiteMemberAuthUpdateRequest(memberAuthKakaoUserWithUuid.getOriginalMemberUuid(), memberAuthKakaoUser.getEmail(), memberAuthKakaoUser.getPw());
}
