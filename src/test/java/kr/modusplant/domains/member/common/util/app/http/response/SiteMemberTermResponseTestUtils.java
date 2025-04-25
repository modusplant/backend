package kr.modusplant.domains.member.common.util.app.http.response;

import kr.modusplant.domains.member.app.http.response.SiteMemberTermResponse;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTermTestUtils;

public interface SiteMemberTermResponseTestUtils extends SiteMemberTermTestUtils {
    SiteMemberTermResponse memberTermUserResponse = new SiteMemberTermResponse(memberTermUserWithUuid.getUuid(), memberTermUser.getAgreedTermsOfUseVersion(), memberTermUser.getAgreedPrivacyPolicyVersion(), memberTermUser.getAgreedAdInfoReceivingVersion());
}
