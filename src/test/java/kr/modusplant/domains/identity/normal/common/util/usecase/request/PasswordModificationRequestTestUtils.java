package kr.modusplant.domains.identity.normal.common.util.usecase.request;

import kr.modusplant.domains.identity.normal.usecase.request.PasswordModificationRequest;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_ADMIN_PW;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;

public interface PasswordModificationRequestTestUtils {
    PasswordModificationRequest testPasswordModificationRequest = new PasswordModificationRequest(
            MEMBER_AUTH_BASIC_USER_PW, MEMBER_AUTH_BASIC_ADMIN_PW
    );
}
