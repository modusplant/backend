package kr.modusplant.domains.account.social.common.util.domain.vo;

import kr.modusplant.domains.account.social.domain.vo.SocialProfile;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;

import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_GOOGLE_PROVIDER_ID_STRING;
import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_PROVIDER_ID_STRING;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.*;

public interface SocialProfileTestUtils extends EmailTestUtils {
    SocialProfile testKakaoSocialProfile = SocialProfile.create(SocialProvider.KAKAO,TEST_SOCIAL_KAKAO_PROVIDER_ID_STRING,testKakaoUserEmail,MEMBER_KAKAO_USER_NICKNAME);
    SocialProfile testGoogleSocialProfile = SocialProfile.create(SocialProvider.GOOGLE,TEST_SOCIAL_GOOGLE_PROVIDER_ID_STRING, testGoogleUserEmail, MEMBER_GOOGLE_USER_NICKNAME);
    SocialProfile testKakaoSocialProfileWithBasicEmail = SocialProfile.create(SocialProvider.KAKAO,TEST_SOCIAL_KAKAO_PROVIDER_ID_STRING,testNormalUserEmail,MEMBER_BASIC_USER_NICKNAME);
    SocialProfile testGoogleSocialProfileWithBasicEmail = SocialProfile.create(SocialProvider.GOOGLE,TEST_SOCIAL_GOOGLE_PROVIDER_ID_STRING, testNormalUserEmail, MEMBER_BASIC_USER_NICKNAME);
}
