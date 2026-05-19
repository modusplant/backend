package kr.modusplant.domains.term.common.util.usecase.request;

import kr.modusplant.domains.term.usecase.request.SiteMemberTermUpdateRequest;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.term.common.constant.MemberTermConstant.*;

public interface SiteMemberTermUpdateRequestTestUtils {
    SiteMemberTermUpdateRequest testSiteMemberTermUpdateRequest = new SiteMemberTermUpdateRequest(
            MEMBER_BASIC_USER_UUID,
            MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION,
            MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION,
            MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION
    );
}
