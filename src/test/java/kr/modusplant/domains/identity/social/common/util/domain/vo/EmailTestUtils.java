package kr.modusplant.domains.identity.social.common.util.domain.vo;

import kr.modusplant.domains.identity.social.domain.vo.Email;

import static kr.modusplant.domains.identity.social.common.constant.SocialStringConstant.TEST_SOCIAL_GOOGLE_EMAIL_STRING;
import static kr.modusplant.domains.identity.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_EMAIL_STRING;

public interface EmailTestUtils {
    Email testSocialKakaoEmail = Email.create(TEST_SOCIAL_KAKAO_EMAIL_STRING);
    Email testSocialGoogleEmail = Email.create(TEST_SOCIAL_GOOGLE_EMAIL_STRING);
}
