package kr.modusplant.domains.term.common.util.usecase.request;

import kr.modusplant.domains.term.usecase.request.SiteMemberTermCreateRequest;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.*;

public interface SiteMemberTermCreateRequestTestUtils {
    SiteMemberTermCreateRequest testSiteMemberTermCreateRequest = new SiteMemberTermCreateRequest(
            MEMBER_BASIC_USER_UUID,
            MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION,
            MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION,
            MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION
    );
}
