package kr.modusplant.domains.normalidentity.normal.common.util.domain.vo;

import kr.modusplant.domains.normalidentity.normal.common.util.usecase.request.NormalSignUpRequestTestUtils;
import kr.modusplant.domains.normalidentity.normal.domain.vo.SignUpData;

public interface SignUpDataTestUtils extends EmailTestUtils, PasswordTestUtils,
        NicknameTestUtils, NormalSignUpRequestTestUtils {
    SignUpData testSignUpData = SignUpData.create(testEmail.getEmail(), testPassword.getPassword(),
            testNickname.getNickname(), testNormalSignUpRequest.agreedTermsOfUseVersion(),
            testNormalSignUpRequest.agreedPrivacyPolicyVersion(), testNormalSignUpRequest.agreedAdInfoReceivingVersion());
}
