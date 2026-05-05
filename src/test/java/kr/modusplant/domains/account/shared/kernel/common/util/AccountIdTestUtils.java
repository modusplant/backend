package kr.modusplant.domains.account.shared.kernel.common.util;

import kr.modusplant.domains.account.shared.kernel.AccountId;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.*;

public interface AccountIdTestUtils {
    AccountId testNormalMemberId = AccountId.create(MEMBER_BASIC_USER_UUID);
    AccountId testKakaoAccountId = AccountId.fromUuid(MEMBER_KAKAO_USER_UUID);
    AccountId testGoogleAccountId = AccountId.fromUuid(MEMBER_GOOGLE_USER_UUID);
}
