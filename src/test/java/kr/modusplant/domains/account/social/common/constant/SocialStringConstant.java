package kr.modusplant.domains.account.social.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_GOOGLE_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_KAKAO_USER_NICKNAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocialStringConstant {
    public static final String TEST_SOCIAL_KAKAO_MEMBER_UUID_STRING = "4f9e87cd-ca94-4ca0-b32b-8f492ee4b93f";
    public static final String TEST_SOCIAL_GOOGLE_MEMBER_UUID_STRING = "6ba6176c-bbc5-4767-9a25-598631918365";
    public static final String TEST_SOCIAL_KAKAO_EMAIL_STRING = MEMBER_AUTH_KAKAO_USER_EMAIL;
    public static final String TEST_SOCIAL_GOOGLE_EMAIL_STRING = MEMBER_AUTH_GOOGLE_USER_EMAIL;
    public static final String TEST_SOCIAL_KAKAO_NICKNAME_STRING = MEMBER_KAKAO_USER_NICKNAME;
    public static final String TEST_SOCIAL_GOOGLE_NICKNAME_STRING = MEMBER_GOOGLE_USER_NICKNAME;
    public static final String TEST_SOCIAL_KAKAO_PROVIDER_ID_STRING = MEMBER_AUTH_KAKAO_USER_PROVIDER_ID;
    public static final String TEST_SOCIAL_GOOGLE_PROVIDER_ID_STRING = MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID;
    public static final String TEST_SOCIAL_KAKAO_CODE = "BPAlKjanydCLdDnYdib6MQpDRwPG7hgqgWwECDwlr_jVWR6WpNeIbGlpBKIKKiVOAAABjE6Zt5qBPKUF0hG4dQ";
    public static final String TEST_SOCIAL_GOOGLE_CODE = "4/0AeanS0aR8pGvH3yN6z5m8Kx7Qw3sT9uV1bC2dE3fG4hI5jK6lM7nO8pQ9rR0sS1tT2u";

}
