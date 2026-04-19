package kr.modusplant.domains.account.social.common.util.domain.vo;

import kr.modusplant.domains.account.social.domain.vo.SocialProfile;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_KAKAO_USER_PROVIDER_ID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.*;

public interface SocialProfileTestUtils extends EmailTestUtils {
    SocialProfile testKakaoSocialProfile = SocialProfile.create(SocialProvider.KAKAO, MEMBER_AUTH_KAKAO_USER_PROVIDER_ID,testKakaoUserEmail,MEMBER_KAKAO_USER_NICKNAME);
    SocialProfile testGoogleSocialProfile = SocialProfile.create(SocialProvider.GOOGLE, MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID, testGoogleUserEmail, MEMBER_GOOGLE_USER_NICKNAME);
    SocialProfile testKakaoSocialProfileWithBasicEmail = SocialProfile.create(SocialProvider.KAKAO, MEMBER_AUTH_KAKAO_USER_PROVIDER_ID,testNormalUserEmail,MEMBER_BASIC_USER_NICKNAME);
    SocialProfile testGoogleSocialProfileWithBasicEmail = SocialProfile.create(SocialProvider.GOOGLE, MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID, testNormalUserEmail, MEMBER_BASIC_USER_NICKNAME);
}
