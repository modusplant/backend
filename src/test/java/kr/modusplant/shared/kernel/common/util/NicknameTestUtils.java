package kr.modusplant.shared.kernel.common.util;

import kr.modusplant.shared.kernel.Nickname;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.*;

public interface NicknameTestUtils {
    Nickname testNormalUserNickname = Nickname.create(MEMBER_BASIC_USER_NICKNAME);
    Nickname testGoogleUserNickname = Nickname.create(MEMBER_GOOGLE_USER_NICKNAME);
    Nickname testKakaoUserNickname = Nickname.create(MEMBER_KAKAO_USER_NICKNAME);
}
