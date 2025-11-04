package kr.modusplant.domains.identity.social.common.util.domain.vo;

import kr.modusplant.domains.identity.social.domain.vo.SocialUserProfile;

public interface SocialUserProfileTestUtils extends SocialCredentialsTestUtils, EmailTestUtils, NicknameTestUtils {
    SocialUserProfile testKakaoSocialUserProfile = SocialUserProfile.create(testKakaoSocialCredentials, testSocialKakaoEmail, testSocialKakaoNickname);
    SocialUserProfile testGoogleSocialUserProfile = SocialUserProfile.create(testGoogleSocialCredentials, testSocialGoogleEmail, testSocialGoogleNickname);
}
