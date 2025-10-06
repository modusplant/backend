package kr.modusplant.domains.identity.common.utils.usecase.request;

import kr.modusplant.domains.identity.common.utils.domain.vo.EmailTestUtils;
import kr.modusplant.domains.identity.common.utils.domain.vo.NicknameTestUtils;
import kr.modusplant.domains.identity.common.utils.domain.vo.PasswordTestUtils;
import kr.modusplant.domains.identity.usecase.request.NormalSignUpRequest;

public interface NormalSignUpRequestTestUtils extends EmailTestUtils, PasswordTestUtils, NicknameTestUtils {
    NormalSignUpRequest testNormalSignUpRequest = new NormalSignUpRequest(
            testEmail.getEmail(), testPassword.getPassword(), testNickname.getNickname(),
            "v1.0.12", "v1.1.3", "v2.0.7"
    );
}
