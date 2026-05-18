package kr.modusplant.domains.account.normal.common.util.usecase.request;

import kr.modusplant.domains.account.normal.usecase.request.EmailModificationRequest;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_ADMIN_EMAIL;
import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_EMAIL;

public interface EmailModificationRequestTestUtils {

    EmailModificationRequest testEmailModificationRequest = new EmailModificationRequest(
            MEMBER_AUTH_BASIC_USER_EMAIL, MEMBER_AUTH_BASIC_ADMIN_EMAIL
    );
}
