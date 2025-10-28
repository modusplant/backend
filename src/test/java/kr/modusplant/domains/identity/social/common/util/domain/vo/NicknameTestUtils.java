package kr.modusplant.domains.identity.social.common.util.domain.vo;

import kr.modusplant.domains.identity.social.domain.vo.Nickname;

import static kr.modusplant.domains.identity.social.common.constant.SocialStringConstant.TEST_SOCIAL_GOOGLE_NICKNAME_STRING;
import static kr.modusplant.domains.identity.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_NICKNAME_STRING;

public interface NicknameTestUtils {
    Nickname testSocialKakaoNickname = Nickname.create(TEST_SOCIAL_KAKAO_NICKNAME_STRING);
    Nickname testSocialGoogleNickname = Nickname.create(TEST_SOCIAL_GOOGLE_NICKNAME_STRING);
}
