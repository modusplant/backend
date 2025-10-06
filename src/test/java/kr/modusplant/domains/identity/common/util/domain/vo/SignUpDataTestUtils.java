package kr.modusplant.domains.identity.common.util.domain.vo;

import kr.modusplant.domains.identity.common.util.usecase.request.NormalSignUpRequestTestUtils;
import kr.modusplant.domains.identity.domain.vo.SignUpData;

public interface SignUpDataTestUtils extends EmailTestUtils, PasswordTestUtils,
        NicknameTestUtils, NormalSignUpRequestTestUtils {
    SignUpData testSignUpData = SignUpData.create(testEmail.getEmail(), testPassword.getPassword(),
            testNickname.getNickname(), testNormalSignUpRequest.agreedTermsOfUseVersion(),
            testNormalSignUpRequest.agreedPrivacyPolicyVersion(), testNormalSignUpRequest.agreedAdInfoReceivingVersion());
}
