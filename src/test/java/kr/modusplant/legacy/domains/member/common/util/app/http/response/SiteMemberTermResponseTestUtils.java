package kr.modusplant.legacy.domains.member.common.util.app.http.response;

import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberTermResponse;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberTermConstant;

public interface SiteMemberTermResponseTestUtils extends SiteMemberTermConstant {
    SiteMemberTermResponse memberTermUserResponse = new SiteMemberTermResponse(memberTermUserWithUuid.getUuid(), memberTermUser.getAgreedTermsOfUseVersion(), memberTermUser.getAgreedPrivacyPolicyVersion(), memberTermUser.getAgreedAdInfoReceivingVersion());
}
