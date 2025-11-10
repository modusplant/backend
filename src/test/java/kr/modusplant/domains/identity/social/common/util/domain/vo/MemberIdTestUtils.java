package kr.modusplant.domains.identity.social.common.util.domain.vo;

import kr.modusplant.domains.identity.social.domain.vo.MemberId;

import static kr.modusplant.domains.identity.social.common.constant.SocialUuidConstant.TEST_SOCIAL_GOOGLE_MEMBER_ID_UUID;
import static kr.modusplant.domains.identity.social.common.constant.SocialUuidConstant.TEST_SOCIAL_KAKAO_MEMBER_ID_UUID;

public interface MemberIdTestUtils {
    MemberId testSocialKakaoMemberId = MemberId.fromUuid(TEST_SOCIAL_KAKAO_MEMBER_ID_UUID);
    MemberId testSocialGoogleMemberId = MemberId.fromUuid(TEST_SOCIAL_GOOGLE_MEMBER_ID_UUID);
}
