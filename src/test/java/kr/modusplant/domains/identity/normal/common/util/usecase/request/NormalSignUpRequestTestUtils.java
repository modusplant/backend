package kr.modusplant.domains.identity.normal.common.util.usecase.request;

import kr.modusplant.domains.identity.normal.common.util.domain.vo.AgreedTermsOfVersionTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.EmailTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.PasswordTestUtils;
import kr.modusplant.domains.identity.normal.usecase.request.NormalSignUpRequest;
import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;

public interface NormalSignUpRequestTestUtils extends AgreedTermsOfVersionTestUtils, EmailTestUtils,
        PasswordTestUtils, NicknameTestUtils {
    NormalSignUpRequest testNormalSignUpRequest = new NormalSignUpRequest(
            testEmail.getValue(), TEST_NORMAL_PASSWORD.getValue(), testNormalUserNickname.getValue(),
            testAgreedTermsOfUse.getValue(), testAgreedPrivacyPolicy.getValue(), testAgreedAdReceiving.getValue()
    );
}
