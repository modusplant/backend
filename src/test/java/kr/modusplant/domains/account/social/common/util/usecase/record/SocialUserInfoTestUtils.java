package kr.modusplant.domains.account.social.common.util.usecase.record;

import kr.modusplant.domains.account.social.usecase.record.SocialUserInfo;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.*;
import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_GOOGLE_SOCIAL_ACCESS_TOKEN;
import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN;

public interface SocialUserInfoTestUtils {
    default SocialUserInfo createKakaoSocialUserInfo() {
        return new SocialUserInfo(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN, MEMBER_AUTH_KAKAO_USER_PROVIDER_ID, MEMBER_AUTH_KAKAO_USER_EMAIL, "kakao-nickname");
    }

    default SocialUserInfo createGoogleSocialUserInfo() {
        return new SocialUserInfo(TEST_SOCIAL_GOOGLE_SOCIAL_ACCESS_TOKEN, MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID, MEMBER_AUTH_GOOGLE_USER_EMAIL, "google-nickname");
    }

    default SocialUserInfo createKakaoSocialUserInfoWithBasicEmail() {
        return new SocialUserInfo(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN, MEMBER_AUTH_KAKAO_USER_PROVIDER_ID, MEMBER_AUTH_BASIC_USER_EMAIL, "kakao-nickname");
    }

    default SocialUserInfo createGoogleSocialUserInfoWithBasicEmail() {
        return new SocialUserInfo(TEST_SOCIAL_GOOGLE_SOCIAL_ACCESS_TOKEN, MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID, MEMBER_AUTH_BASIC_USER_EMAIL, "google-nickname");
    }
}
