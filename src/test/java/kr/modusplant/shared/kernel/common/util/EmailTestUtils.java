package kr.modusplant.shared.kernel.common.util;

import kr.modusplant.shared.kernel.Email;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.*;

public interface EmailTestUtils {
    Email testNormalUserEmail = Email.create(MEMBER_AUTH_BASIC_USER_EMAIL);
    Email testKakaoUserEmail = Email.create(MEMBER_AUTH_KAKAO_USER_EMAIL);
    Email testGoogleUserEmail = Email.create(MEMBER_AUTH_GOOGLE_USER_EMAIL);
}
