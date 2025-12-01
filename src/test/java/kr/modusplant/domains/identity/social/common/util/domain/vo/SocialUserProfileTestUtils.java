package kr.modusplant.domains.identity.social.common.util.domain.vo;

import kr.modusplant.domains.identity.social.domain.vo.SocialUserProfile;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;

public interface SocialUserProfileTestUtils extends SocialCredentialsTestUtils, EmailTestUtils, NicknameTestUtils {
    SocialUserProfile testKakaoSocialUserProfile = SocialUserProfile.create(testKakaoSocialCredentials, testKakaoUserEmail, testSocialKakaoNickname);
    SocialUserProfile testGoogleSocialUserProfile = SocialUserProfile.create(testGoogleSocialCredentials, testGoogleUserEmail, testSocialGoogleNickname);
}
