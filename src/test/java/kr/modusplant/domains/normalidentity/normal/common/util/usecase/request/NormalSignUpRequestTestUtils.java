package kr.modusplant.domains.normalidentity.normal.common.util.usecase.request;

import kr.modusplant.domains.identity.normal.usecase.request.NormalSignUpRequest;
import kr.modusplant.domains.normalidentity.normal.common.util.domain.vo.EmailTestUtils;
import kr.modusplant.domains.normalidentity.normal.common.util.domain.vo.NicknameTestUtils;
import kr.modusplant.domains.normalidentity.normal.common.util.domain.vo.PasswordTestUtils;

public interface NormalSignUpRequestTestUtils extends EmailTestUtils, PasswordTestUtils, NicknameTestUtils {
    NormalSignUpRequest testNormalSignUpRequest = new NormalSignUpRequest(
            testEmail.getEmail(), testPassword.getPassword(), testNickname.getNickname(),
            "v1.0.12", "v1.1.3", "v2.0.7"
    );
}
