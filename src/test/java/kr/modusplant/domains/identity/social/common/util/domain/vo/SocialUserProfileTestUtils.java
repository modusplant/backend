package kr.modusplant.domains.identity.social.common.util.domain.vo;

import kr.modusplant.domains.identity.social.domain.vo.SocialUserProfile;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;
import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;

public interface SocialUserProfileTestUtils extends SocialCredentialsTestUtils, EmailTestUtils, NicknameTestUtils {
    SocialUserProfile testKakaoSocialUserProfile = SocialUserProfile.create(testKakaoSocialCredentials, testKakaoUserEmail, testKakaoUserNickname);
    SocialUserProfile testGoogleSocialUserProfile = SocialUserProfile.create(testGoogleSocialCredentials, testGoogleUserEmail, testGoogleUserNickname);
}
