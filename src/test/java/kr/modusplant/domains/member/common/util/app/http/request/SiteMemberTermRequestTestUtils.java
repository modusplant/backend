package kr.modusplant.domains.member.common.util.app.http.request;

import kr.modusplant.domains.member.app.http.request.SiteMemberTermInsertRequest;
import kr.modusplant.domains.member.app.http.request.SiteMemberTermUpdateRequest;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTermTestUtils;

public interface SiteMemberTermRequestTestUtils extends SiteMemberTermTestUtils {
    SiteMemberTermInsertRequest memberTermUserInsertRequest = new SiteMemberTermInsertRequest(memberTermUserWithUuid.getUuid(), memberTermUser.getAgreedTermsOfUseVersion(), memberTermUser.getAgreedPrivacyPolicyVersion(), memberTermUser.getAgreedAdInfoReceivingVersion());

    SiteMemberTermUpdateRequest memberTermUserUpdateRequest = new SiteMemberTermUpdateRequest(memberTermUserWithUuid.getUuid(), memberTermUser.getAgreedTermsOfUseVersion(), memberTermUser.getAgreedPrivacyPolicyVersion(), memberTermUser.getAgreedAdInfoReceivingVersion());
}
