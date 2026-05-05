package kr.modusplant.shared.persistence.common.util.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static kr.modusplant.shared.util.VersionUtils.createVersion;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SiteMemberTermConstant {
    public static final String MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION = createVersion(1, 0, 100);
    public static final String MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION = createVersion(2, 1, 102);
    public static final String MEMBER_TERM_ADMIN_AGREED_COMMUNITY_POLICY_VERSION = createVersion(3, 2, 104);

    public static final String MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION = createVersion(1, 1, 100);
    public static final String MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION = createVersion(2, 2, 102);
    public static final String MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION = createVersion(3, 3, 104);
}