package kr.modusplant.domains.normalidentity.normal.common.util.usecase.request;

import kr.modusplant.domains.identity.normal.usecase.request.NormalSignUpRequest;
import kr.modusplant.domains.normalidentity.normal.common.util.domain.vo.NicknameTestUtils;
import kr.modusplant.domains.normalidentity.normal.common.util.domain.vo.PasswordTestUtils;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;

public interface NormalSignUpRequestTestUtils extends EmailTestUtils, PasswordTestUtils, NicknameTestUtils {
    NormalSignUpRequest testNormalSignUpRequest = new NormalSignUpRequest(
            testNormalUserEmail.getEmail(), testPassword.getPassword(), testNickname.getNickname(),
            "v1.0.12", "v1.1.3", "v2.0.7"
    );
}
