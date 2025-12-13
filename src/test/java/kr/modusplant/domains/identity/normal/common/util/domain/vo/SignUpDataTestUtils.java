package kr.modusplant.domains.identity.normal.common.util.domain.vo;

import kr.modusplant.domains.identity.normal.common.util.usecase.request.NormalSignUpRequestTestUtils;
import kr.modusplant.domains.identity.normal.domain.vo.SignUpData;
import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;
import kr.modusplant.shared.kernel.common.util.PasswordTestUtils;

public interface SignUpDataTestUtils extends EmailTestUtils, PasswordTestUtils,
        NicknameTestUtils, NormalSignUpRequestTestUtils {
    SignUpData TEST_NORMAL_SIGN_UP_DATA = SignUpData.create(testEmail.getValue(), testNormalUserPassword.getValue(),
            testNormalUserNickname.getValue(), testNormalSignUpRequest.agreedTermsOfUseVersion(),
            testNormalSignUpRequest.agreedPrivacyPolicyVersion(), testNormalSignUpRequest.agreedAdInfoReceivingVersion());
}
