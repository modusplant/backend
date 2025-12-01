package kr.modusplant.domains.identity.normal.common.util.domain.vo;

import kr.modusplant.domains.identity.normal.domain.vo.Password;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;

public interface PasswordTestUtils {
    Password testPassword = Password.create(MEMBER_AUTH_BASIC_USER_PW);
}
