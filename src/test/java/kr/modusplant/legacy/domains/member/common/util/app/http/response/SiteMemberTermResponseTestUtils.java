package kr.modusplant.legacy.domains.member.common.util.app.http.response;

import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberTermResponse;

import static kr.modusplant.shared.persistence.common.constant.SiteMemberTermConstant.*;

public interface SiteMemberTermResponseTestUtils {
    SiteMemberTermResponse memberTermUserResponse = new SiteMemberTermResponse(MEMBER_TERM_USER_UUID, MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION, MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION, MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION);
}
