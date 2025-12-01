package kr.modusplant.domains.identity.normal.common.util.domain.vo;

import kr.modusplant.domains.identity.normal.domain.vo.Email;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_EMAIL;

public interface EmailTestUtils {
    Email testEmail = Email.create(MEMBER_AUTH_BASIC_USER_EMAIL);
}
