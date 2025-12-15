package kr.modusplant.shared.kernel.common.util;

import kr.modusplant.shared.kernel.Password;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;

public interface PasswordTestUtils {
    Password testNormalUserPassword = Password.create(MEMBER_AUTH_BASIC_USER_PW);
}
