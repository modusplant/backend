package kr.modusplant.domains.account.social.common.util.domain.vo;

import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.shared.enums.AuthProvider;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_KAKAO_USER_PROVIDER_ID;

public interface SocialCredentialsTestUtils {
    SocialCredentials testBasicSocialCredentials = SocialCredentials.create(AuthProvider.BASIC,null);
    SocialCredentials testKakaoSocialCredentials = SocialCredentials.createKakao(MEMBER_AUTH_KAKAO_USER_PROVIDER_ID);
    SocialCredentials testGoogleSocialCredentials = SocialCredentials.createGoogle(MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID);
    SocialCredentials testBasicKakaoSocialCredentials = SocialCredentials.createBasicKakao(MEMBER_AUTH_KAKAO_USER_PROVIDER_ID);
    SocialCredentials testBasicGoogleSocialCredentials = SocialCredentials.createBasicGoogle(MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID);
}
