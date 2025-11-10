package kr.modusplant.domains.identity.social.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_GOOGLE_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_KAKAO_USER_UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocialUuidConstant {
    public static final UUID TEST_SOCIAL_KAKAO_MEMBER_ID_UUID = MEMBER_KAKAO_USER_UUID;
    public static final UUID TEST_SOCIAL_GOOGLE_MEMBER_ID_UUID = MEMBER_GOOGLE_USER_UUID;
}
