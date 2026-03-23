package kr.modusplant.domains.term.common.util.usecase.request;

import kr.modusplant.domains.term.usecase.request.SiteMemberTermUpdateRequest;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.*;

public interface SiteMemberTermUpdateRequestTestUtils {
    SiteMemberTermUpdateRequest testSiteMemberTermUpdateRequest = new SiteMemberTermUpdateRequest(
            MEMBER_TERM_USER_UUID,
            MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION,
            MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION,
            MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION
    );
}
