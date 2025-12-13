package kr.modusplant.domains.identity.shared.kernel.common.util;

import kr.modusplant.domains.identity.shared.kernel.AccountId;

import static kr.modusplant.domains.identity.social.common.constant.SocialUuidConstant.TEST_SOCIAL_GOOGLE_MEMBER_ID_UUID;
import static kr.modusplant.domains.identity.social.common.constant.SocialUuidConstant.TEST_SOCIAL_KAKAO_MEMBER_ID_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface AccountIdTestUtils {
    AccountId testNormalMemberId = AccountId.create(MEMBER_BASIC_USER_UUID);
    AccountId testKakaoAccountId = AccountId.fromUuid(TEST_SOCIAL_KAKAO_MEMBER_ID_UUID);
    AccountId testGoogleAccountId = AccountId.fromUuid(TEST_SOCIAL_GOOGLE_MEMBER_ID_UUID);
}
