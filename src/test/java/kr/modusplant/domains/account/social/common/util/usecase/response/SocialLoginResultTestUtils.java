package kr.modusplant.domains.account.social.common.util.usecase.response;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.response.LoginResult;
import kr.modusplant.domains.account.social.usecase.response.NeedLinkResult;
import kr.modusplant.domains.account.social.usecase.response.NeedSignupResult;
import kr.modusplant.shared.enums.Role;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.*;

public interface SocialLoginResultTestUtils {
    default LoginResult createKakaoLoginResult() {
        return new LoginResult(MEMBER_KAKAO_USER_UUID,MEMBER_AUTH_KAKAO_USER_EMAIL,MEMBER_KAKAO_USER_NICKNAME, Role.USER);
    }

    default LoginResult createGoogleLoginResult() {
        return new LoginResult(MEMBER_GOOGLE_USER_UUID,MEMBER_AUTH_GOOGLE_USER_EMAIL,MEMBER_GOOGLE_USER_NICKNAME, Role.USER);
    }

    default LoginResult createBasicSocialLoginResult() {
        return new LoginResult(MEMBER_BASIC_USER_UUID,MEMBER_AUTH_BASIC_USER_EMAIL,MEMBER_BASIC_USER_NICKNAME,Role.USER);
    }

    default NeedSignupResult createKakaoNeedSignupResult() {
        return new NeedSignupResult(MEMBER_AUTH_KAKAO_USER_EMAIL, MEMBER_KAKAO_USER_NICKNAME, MEMBER_AUTH_KAKAO_USER_PROVIDER_ID, SocialProvider.KAKAO);
    }

    default NeedSignupResult createGoogleNeedSignupResult() {
        return new NeedSignupResult(MEMBER_AUTH_GOOGLE_USER_EMAIL ,MEMBER_GOOGLE_USER_NICKNAME, MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID,SocialProvider.GOOGLE);
    }

    default NeedLinkResult createKakaoNeedLinkResult() {
        return new NeedLinkResult(MEMBER_AUTH_BASIC_USER_EMAIL,MEMBER_BASIC_USER_NICKNAME, MEMBER_AUTH_KAKAO_USER_PROVIDER_ID,SocialProvider.KAKAO);
    }

    default NeedLinkResult createGoogleNeedLinkResult() {
        return new NeedLinkResult(MEMBER_AUTH_BASIC_USER_EMAIL,MEMBER_BASIC_USER_NICKNAME, MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID,SocialProvider.GOOGLE);
    }
}
