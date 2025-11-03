package kr.modusplant.domains.identity.social.common.util.usecase.request;

import kr.modusplant.domains.identity.social.usecase.request.SocialLoginRequest;

import static kr.modusplant.domains.identity.social.common.constant.SocialStringConstant.TEST_SOCIAL_GOOGLE_CODE;
import static kr.modusplant.domains.identity.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_CODE;

public interface SocialLoginRequestTestUtils {
    default SocialLoginRequest createTestKakaoLoginRequest() {
        SocialLoginRequest request = new SocialLoginRequest();
        request.setCode(TEST_SOCIAL_KAKAO_CODE);
        return request;
    }

    default SocialLoginRequest createTestGoogleLoginRequest() {
        SocialLoginRequest request = new SocialLoginRequest();
        request.setCode(TEST_SOCIAL_GOOGLE_CODE);
        return request;
    }
}
