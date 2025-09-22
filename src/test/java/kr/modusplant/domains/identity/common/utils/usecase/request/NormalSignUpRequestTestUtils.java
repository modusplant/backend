package kr.modusplant.domains.identity.common.utils.usecase.request;

import kr.modusplant.domains.identity.usecase.request.NormalSignUpRequest;

public interface NormalSignUpRequestTestUtils {
    NormalSignUpRequest testNormalSignUpRequest = new NormalSignUpRequest(
            "test123@example.com", "userPw2!", "테스트닉네임",
            "v1.0.12", "v1.1.3", "v2.0.7"
    );
}
