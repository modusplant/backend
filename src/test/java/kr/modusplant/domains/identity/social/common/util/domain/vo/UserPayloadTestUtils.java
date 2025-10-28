package kr.modusplant.domains.identity.social.common.util.domain.vo;

import kr.modusplant.domains.identity.social.domain.vo.UserPayload;
import kr.modusplant.infrastructure.security.enums.Role;

public interface UserPayloadTestUtils extends MemberIdTestUtils, NicknameTestUtils {
    UserPayload testSocialKakaoUserPayload = UserPayload.create(testSocialKakaoMemberId,testSocialKakaoNickname,Role.USER);
    UserPayload testSocialGoogleUserPayload = UserPayload.create(testSocialGoogleMemberId,testSocialGoogleNickname,Role.USER);
}
