package kr.modusplant.legacy.domains.member.common.util.app.http.request;

import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberUpdateRequest;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberConstant;

public interface SiteMemberRequestTestUtils extends SiteMemberConstant {
    SiteMemberInsertRequest memberBasicUserInsertRequest = new SiteMemberInsertRequest(memberBasicUser.getNickname());

    SiteMemberUpdateRequest memberBasicUserUpdateRequest = new SiteMemberUpdateRequest(memberBasicUserWithUuid.getUuid(), memberBasicUser.getNickname(), memberBasicUser.getBirthDate());

    SiteMemberInsertRequest memberGoogleUserInsertRequest = new SiteMemberInsertRequest(memberGoogleUser.getNickname());

    SiteMemberUpdateRequest memberGoogleUserUpdateRequest = new SiteMemberUpdateRequest(memberGoogleUserWithUuid.getUuid(), memberGoogleUser.getNickname(), memberGoogleUser.getBirthDate());

    SiteMemberInsertRequest memberKakaoUserInsertRequest = new SiteMemberInsertRequest(memberKakaoUser.getNickname());

    SiteMemberUpdateRequest memberKakaoUserUpdateRequest = new SiteMemberUpdateRequest(memberKakaoUserWithUuid.getUuid(), memberKakaoUser.getNickname(), memberKakaoUser.getBirthDate());
}
