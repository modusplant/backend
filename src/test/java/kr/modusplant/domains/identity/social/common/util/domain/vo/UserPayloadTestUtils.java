package kr.modusplant.domains.identity.social.common.util.domain.vo;

import kr.modusplant.domains.identity.social.domain.vo.SocialAccountPayload;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;
import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;

public interface UserPayloadTestUtils extends MemberIdTestUtils, NicknameTestUtils, EmailTestUtils {
    SocialAccountPayload TEST_SOCIAL_KAKAO_SOCIAL_ACCOUNT_PAYLOAD = SocialAccountPayload.create(testSocialKakaoMemberId, testKakaoUserNickname, testKakaoUserEmail, Role.USER);
    SocialAccountPayload TEST_SOCIAL_GOOGLE_SOCIAL_ACCOUNT_PAYLOAD = SocialAccountPayload.create(testSocialGoogleMemberId, testGoogleUserNickname, testGoogleUserEmail, Role.USER);
}
