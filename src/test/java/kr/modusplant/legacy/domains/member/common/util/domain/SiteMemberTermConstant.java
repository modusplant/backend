package kr.modusplant.legacy.domains.member.common.util.domain;

import java.util.UUID;

import static kr.modusplant.shared.util.VersionUtils.createVersion;

public interface SiteMemberTermConstant extends SiteMemberConstant {
    UUID MEMBER_TERM_ADMIN_UUID = MEMBER_BASIC_ADMIN_UUID;
    String MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION = createVersion(1, 0, 0);
    String MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION = createVersion(2, 1, 2);
    String MEMBER_TERM_ADMIN_AGREED_AD_INFO_RECEIVING_VERSION = createVersion(3, 2, 4);

    UUID MEMBER_TERM_USER_UUID = MEMBER_BASIC_USER_UUID;
    String MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION = createVersion(1, 1, 0);
    String MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION = createVersion(2, 2, 2);
    String MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION = createVersion(3, 3, 4);
}