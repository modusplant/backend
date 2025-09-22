package kr.modusplant.domains.identity.common.utils.domain.vo;

import kr.modusplant.domains.identity.common.utils.usecase.request.NormalSignUpRequestTestUtils;
import kr.modusplant.domains.identity.domain.vo.SignUpData;

public interface SignUpDataTestUtils extends CredentialsTestUtils, NicknameTestUtils, NormalSignUpRequestTestUtils {
    SignUpData testSignUpData = SignUpData.create(testCredentials.getEmail(), testCredentials.getPassword(),
            testNickname.getNickname(), testNormalSignUpRequest.agreedTermsOfUseVersion(),
            testNormalSignUpRequest.agreedPrivacyPolicyVersion(), testNormalSignUpRequest.agreedAdInfoReceivingVersion());
}
