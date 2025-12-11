package kr.modusplant.domains.identity.normal.common.util.domain.vo;

import kr.modusplant.domains.identity.normal.domain.vo.NormalPassword;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;

public interface PasswordTestUtils {
    NormalPassword TEST_NORMAL_PASSWORD = NormalPassword.create(MEMBER_AUTH_BASIC_USER_PW);
}
