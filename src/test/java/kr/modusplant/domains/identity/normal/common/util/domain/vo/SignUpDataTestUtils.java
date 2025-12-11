package kr.modusplant.domains.identity.normal.common.util.domain.vo;

import kr.modusplant.domains.identity.normal.common.util.usecase.request.NormalSignUpRequestTestUtils;
import kr.modusplant.domains.identity.normal.domain.vo.SignUpData;

public interface SignUpDataTestUtils extends EmailTestUtils, PasswordTestUtils,
        NicknameTestUtils, NormalSignUpRequestTestUtils {
    SignUpData TEST_NORMAL_SIGN_UP_DATA = SignUpData.create(testEmail.getEmail(), TEST_NORMAL_PASSWORD.getValue(),
            TEST_NORMAL_NICKNAME.getValue(), testNormalSignUpRequest.agreedTermsOfUseVersion(),
            testNormalSignUpRequest.agreedPrivacyPolicyVersion(), testNormalSignUpRequest.agreedAdInfoReceivingVersion());
}
