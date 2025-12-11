package kr.modusplant.domains.identity.normal.common.util.usecase.request;

import kr.modusplant.domains.identity.normal.common.util.domain.vo.AgreedTermsOfVersionTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.EmailTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.NicknameTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.PasswordTestUtils;
import kr.modusplant.domains.identity.normal.usecase.request.NormalSignUpRequest;

public interface NormalSignUpRequestTestUtils extends AgreedTermsOfVersionTestUtils, EmailTestUtils,
        PasswordTestUtils, NicknameTestUtils {
    NormalSignUpRequest testNormalSignUpRequest = new NormalSignUpRequest(
            testEmail.getEmail(), TEST_NORMAL_PASSWORD.getValue(), TEST_NORMAL_NICKNAME.getValue(),
            testAgreedTermsOfUse.getValue(), testAgreedPrivacyPolicy.getValue(), testAgreedAdReceiving.getValue()
    );
}
