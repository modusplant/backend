package kr.modusplant.shared.kernel.common.util;

import kr.modusplant.shared.kernel.Email;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.*;

public interface EmailTestUtils {
    Email testNormalUserEmail = Email.create(MEMBER_AUTH_BASIC_USER_EMAIL);
    Email testKakaoUserEmail = Email.create(MEMBER_AUTH_KAKAO_USER_EMAIL);
    Email testGoogleUserEmail = Email.create(MEMBER_AUTH_GOOGLE_USER_EMAIL);
}
