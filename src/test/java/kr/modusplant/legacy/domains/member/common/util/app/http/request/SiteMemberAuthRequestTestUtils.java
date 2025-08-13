package kr.modusplant.legacy.domains.member.common.util.app.http.request;

import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberAuthInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberAuthUpdateRequest;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberAuthTestUtils;

public interface SiteMemberAuthRequestTestUtils extends SiteMemberAuthTestUtils {
    SiteMemberAuthInsertRequest memberAuthBasicUserInsertRequest = new SiteMemberAuthInsertRequest(memberAuthBasicUserWithUuid.getOriginalMemberUuid(), memberAuthBasicUser.getEmail(), memberAuthBasicUser.getPw(), memberAuthBasicUser.getProvider(), memberAuthBasicUser.getProviderId());

    SiteMemberAuthUpdateRequest memberAuthBasicUserUpdateRequest = new SiteMemberAuthUpdateRequest(memberAuthBasicUserWithUuid.getOriginalMemberUuid(), memberAuthBasicUser.getEmail(), memberAuthBasicUser.getPw());

    SiteMemberAuthInsertRequest memberAuthGoogleUserInsertRequest = new SiteMemberAuthInsertRequest(memberAuthGoogleUserWithUuid.getOriginalMemberUuid(), memberAuthGoogleUser.getEmail(), memberAuthGoogleUser.getPw(), memberAuthGoogleUser.getProvider(), memberAuthGoogleUser.getProviderId());

    SiteMemberAuthUpdateRequest memberAuthGoogleUserUpdateRequest = new SiteMemberAuthUpdateRequest(memberAuthGoogleUserWithUuid.getOriginalMemberUuid(), memberAuthGoogleUser.getEmail(), memberAuthGoogleUser.getPw());

    SiteMemberAuthInsertRequest memberAuthKakaoUserInsertRequest = new SiteMemberAuthInsertRequest(memberAuthKakaoUserWithUuid.getOriginalMemberUuid(), memberAuthKakaoUser.getEmail(), memberAuthKakaoUser.getPw(), memberAuthKakaoUser.getProvider(), memberAuthKakaoUser.getProviderId());

    SiteMemberAuthUpdateRequest memberAuthKakaoUserUpdateRequest = new SiteMemberAuthUpdateRequest(memberAuthKakaoUserWithUuid.getOriginalMemberUuid(), memberAuthKakaoUser.getEmail(), memberAuthKakaoUser.getPw());
}
