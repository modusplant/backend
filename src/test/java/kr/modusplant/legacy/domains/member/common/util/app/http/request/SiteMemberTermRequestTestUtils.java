package kr.modusplant.legacy.domains.member.common.util.app.http.request;

import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberTermInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberTermUpdateRequest;

import static kr.modusplant.framework.out.jpa.entity.common.constant.SiteMemberTermEntityConstant.*;

public interface SiteMemberTermRequestTestUtils {
    SiteMemberTermInsertRequest memberTermUserInsertRequest = new SiteMemberTermInsertRequest(MEMBER_TERM_USER_UUID, MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION, MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION, MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION);

    SiteMemberTermUpdateRequest memberTermUserUpdateRequest = new SiteMemberTermUpdateRequest(MEMBER_TERM_USER_UUID, MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION, MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION, MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION);
}
