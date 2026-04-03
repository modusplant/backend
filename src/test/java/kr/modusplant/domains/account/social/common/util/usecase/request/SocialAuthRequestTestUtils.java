package kr.modusplant.domains.account.social.common.util.usecase.request;

import kr.modusplant.domains.account.social.usecase.request.SocialAuthRequest;

import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_GOOGLE_CODE;
import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_CODE;

public interface SocialAuthRequestTestUtils {
    default SocialAuthRequest createTestKakaoLoginRequest() {
        SocialAuthRequest request = new SocialAuthRequest(TEST_SOCIAL_KAKAO_CODE);
        return request;
    }

    default SocialAuthRequest createTestGoogleLoginRequest() {
        SocialAuthRequest request = new SocialAuthRequest(TEST_SOCIAL_GOOGLE_CODE);
        return request;
    }
}
