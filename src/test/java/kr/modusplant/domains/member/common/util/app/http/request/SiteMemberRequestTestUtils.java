package kr.modusplant.domains.member.common.util.app.http.request;

import kr.modusplant.domains.member.app.http.request.SiteMemberInsertRequest;
import kr.modusplant.domains.member.app.http.request.SiteMemberUpdateRequest;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;

public interface SiteMemberRequestTestUtils extends SiteMemberTestUtils {
    SiteMemberInsertRequest memberBasicUserInsertRequest = new SiteMemberInsertRequest(memberBasicUser.getNickname());

    SiteMemberUpdateRequest memberBasicUserUpdateRequest = new SiteMemberUpdateRequest(memberBasicUserWithUuid.getUuid(), memberBasicUser.getNickname(), memberBasicUser.getBirthDate());

    SiteMemberInsertRequest memberGoogleUserInsertRequest = new SiteMemberInsertRequest(memberGoogleUser.getNickname());

    SiteMemberUpdateRequest memberGoogleUserUpdateRequest = new SiteMemberUpdateRequest(memberGoogleUserWithUuid.getUuid(), memberGoogleUser.getNickname(), memberGoogleUser.getBirthDate());

    SiteMemberInsertRequest memberKakaoUserInsertRequest = new SiteMemberInsertRequest(memberKakaoUser.getNickname());

    SiteMemberUpdateRequest memberKakaoUserUpdateRequest = new SiteMemberUpdateRequest(memberKakaoUserWithUuid.getUuid(), memberKakaoUser.getNickname(), memberKakaoUser.getBirthDate());
}
