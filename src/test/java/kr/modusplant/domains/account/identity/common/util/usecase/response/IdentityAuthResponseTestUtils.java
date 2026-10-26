package kr.modusplant.domains.account.identity.common.util.usecase.response;

import kr.modusplant.domains.account.identity.usecase.response.IdentityAuthResponse;

import java.time.LocalDate;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_EMAIL;
import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_PROVIDER;

public interface IdentityAuthResponseTestUtils {

    IdentityAuthResponse testIdentityAuthResponse = new IdentityAuthResponse(
            MEMBER_AUTH_BASIC_USER_EMAIL, MEMBER_AUTH_BASIC_USER_PROVIDER, LocalDate.now()
    );
}
