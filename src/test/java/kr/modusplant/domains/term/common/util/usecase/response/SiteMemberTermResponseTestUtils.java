package kr.modusplant.domains.term.common.util.usecase.response;

import kr.modusplant.domains.term.usecase.response.SiteMemberTermResponse;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.*;

public interface SiteMemberTermResponseTestUtils {
    SiteMemberTermResponse testSiteMemberTermResponse = new SiteMemberTermResponse(
            MEMBER_BASIC_USER_UUID,
            MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION,
            MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION,
            MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION
    );
}
