package kr.modusplant.domains.account.social.common.util.domain.vo;

import kr.modusplant.domains.account.social.domain.vo.SocialAccountPayload;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;
import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;

import static kr.modusplant.domains.account.shared.kernel.common.util.AccountIdTestUtils.testGoogleAccountId;
import static kr.modusplant.domains.account.shared.kernel.common.util.AccountIdTestUtils.testKakaoAccountId;

public interface SocialAccountPayloadTestUtils extends NicknameTestUtils, EmailTestUtils {
    SocialAccountPayload TEST_SOCIAL_KAKAO_SOCIAL_ACCOUNT_PAYLOAD = SocialAccountPayload.create(testKakaoAccountId, testKakaoUserNickname, testKakaoUserEmail, Role.USER);
    SocialAccountPayload TEST_SOCIAL_GOOGLE_SOCIAL_ACCOUNT_PAYLOAD = SocialAccountPayload.create(testGoogleAccountId, testGoogleUserNickname, testGoogleUserEmail, Role.USER);
}
