package kr.modusplant.domains.normalidentity.normal.common.util.domain.vo;

import kr.modusplant.domains.identity.normal.domain.vo.SignUpData;
import kr.modusplant.domains.normalidentity.normal.common.util.usecase.request.NormalSignUpRequestTestUtils;

public interface SignUpDataTestUtils extends EmailTestUtils, PasswordTestUtils,
        NicknameTestUtils, NormalSignUpRequestTestUtils {
    SignUpData testSignUpData = SignUpData.create(testEmail.getEmail(), testPassword.getPassword(),
            testNickname.getNickname(), testNormalSignUpRequest.agreedTermsOfUseVersion(),
            testNormalSignUpRequest.agreedPrivacyPolicyVersion(), testNormalSignUpRequest.agreedAdInfoReceivingVersion());
}
