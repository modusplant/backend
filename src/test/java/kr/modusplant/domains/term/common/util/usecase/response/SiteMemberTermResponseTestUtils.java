package kr.modusplant.domains.term.common.util.usecase.response;

import kr.modusplant.domains.term.usecase.response.SiteMemberTermResponse;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.*;

public interface SiteMemberTermResponseTestUtils {
    SiteMemberTermResponse testSiteMemberTermResponse = new SiteMemberTermResponse(
            MEMBER_TERM_USER_UUID,
            MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION,
            MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION,
            MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION
    );
}
