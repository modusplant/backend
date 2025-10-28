package kr.modusplant.domains.identity.social.common.util.domain.vo;

import kr.modusplant.domains.identity.social.domain.vo.SocialCredentials;

import static kr.modusplant.domains.identity.social.common.constant.SocialStringConstant.TEST_SOCIAL_GOOGLE_PROVIDER_ID_STRING;
import static kr.modusplant.domains.identity.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_PROVIDER_ID_STRING;

public interface SocialCredentialsTestUtils {
    SocialCredentials testKakaoSocialCredentials = SocialCredentials.createKakao(TEST_SOCIAL_KAKAO_PROVIDER_ID_STRING);
    SocialCredentials testGoogleSocialCredentials = SocialCredentials.createGoogle(TEST_SOCIAL_GOOGLE_PROVIDER_ID_STRING);
}
