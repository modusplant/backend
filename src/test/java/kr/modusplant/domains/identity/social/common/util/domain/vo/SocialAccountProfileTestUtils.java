package kr.modusplant.domains.identity.social.common.util.domain.vo;

import kr.modusplant.domains.identity.social.domain.vo.SocialAccountProfile;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;
import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;

public interface SocialAccountProfileTestUtils extends SocialCredentialsTestUtils, EmailTestUtils, NicknameTestUtils {
    SocialAccountProfile TEST_KAKAO_SOCIAL_ACCOUNT_PROFILE = SocialAccountProfile.create(testKakaoSocialCredentials, testKakaoUserEmail, testKakaoUserNickname);
    SocialAccountProfile TEST_GOOGLE_SOCIAL_ACCOUNT_PROFILE = SocialAccountProfile.create(testGoogleSocialCredentials, testGoogleUserEmail, testGoogleUserNickname);
}
