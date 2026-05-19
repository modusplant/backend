package kr.modusplant.domains.account.normal.common.util.usecase.request;

import kr.modusplant.domains.account.normal.usecase.request.PasswordModificationRequest;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_ADMIN_PW;
import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;

public interface PasswordModificationRequestTestUtils {
    PasswordModificationRequest testPasswordModificationRequest = new PasswordModificationRequest(
            MEMBER_AUTH_BASIC_USER_PW, MEMBER_AUTH_BASIC_ADMIN_PW
    );
}
