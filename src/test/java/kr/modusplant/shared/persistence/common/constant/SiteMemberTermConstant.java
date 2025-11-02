package kr.modusplant.shared.persistence.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static kr.modusplant.shared.util.VersionUtils.createVersion;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SiteMemberTermConstant {
    public static final UUID MEMBER_TERM_ADMIN_UUID = SiteMemberConstant.MEMBER_BASIC_ADMIN_UUID;
    public static final String MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION = createVersion(1, 0, 0);
    public static final String MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION = createVersion(2, 1, 2);
    public static final String MEMBER_TERM_ADMIN_AGREED_AD_INFO_RECEIVING_VERSION = createVersion(3, 2, 4);

    public static final UUID MEMBER_TERM_USER_UUID = SiteMemberConstant.MEMBER_BASIC_USER_UUID;
    public static final String MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION = createVersion(1, 1, 0);
    public static final String MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION = createVersion(2, 2, 2);
    public static final String MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION = createVersion(3, 3, 4);
}