package kr.modusplant.domains.identity.normal.common.util.usecase.request;

import kr.modusplant.domains.identity.normal.common.util.domain.vo.AgreedTermsOfVersionTestUtils;
import kr.modusplant.domains.identity.normal.usecase.request.NormalSignUpRequest;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;
import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;
import kr.modusplant.shared.kernel.common.util.PasswordTestUtils;

public interface NormalSignUpRequestTestUtils extends AgreedTermsOfVersionTestUtils, EmailTestUtils,
        PasswordTestUtils, NicknameTestUtils {
    NormalSignUpRequest testNormalSignUpRequest = new NormalSignUpRequest(
            testNormalUserEmail.getValue(), testNormalUserPassword.getValue(), testNormalUserNickname.getValue(),
            testAgreedTermsOfUse.getValue(), testAgreedPrivacyPolicy.getValue(), testAgreedAdReceiving.getValue()
    );
}
