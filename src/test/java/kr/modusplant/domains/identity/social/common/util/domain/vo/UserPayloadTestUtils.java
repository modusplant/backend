package kr.modusplant.domains.identity.social.common.util.domain.vo;

import kr.modusplant.domains.identity.social.domain.vo.UserPayload;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;
import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;

public interface UserPayloadTestUtils extends MemberIdTestUtils, NicknameTestUtils, EmailTestUtils {
    UserPayload testSocialKakaoUserPayload = UserPayload.create(testSocialKakaoMemberId, testKakaoUserNickname, testKakaoUserEmail, Role.USER);
    UserPayload testSocialGoogleUserPayload = UserPayload.create(testSocialGoogleMemberId, testGoogleUserNickname, testGoogleUserEmail, Role.USER);
}
