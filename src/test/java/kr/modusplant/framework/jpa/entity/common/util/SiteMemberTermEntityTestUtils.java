package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.SiteMemberTermEntity;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.*;

public interface SiteMemberTermEntityTestUtils extends SiteMemberEntityTestUtils {
    default SiteMemberTermEntity createMemberTermAdminEntity() {
        return SiteMemberTermEntity.builder()
                .member(createMemberBasicAdminEntity())
                .agreedTermsOfUseVersion(MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION)
                .agreedPrivacyPolicyVersion(MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION)
                .agreedCommunityPolicyVersion(MEMBER_TERM_ADMIN_AGREED_COMMUNITY_POLICY_VERSION)
                .build();
    }

    default SiteMemberTermEntity createMemberTermAdminEntityWithUuid() {
        return SiteMemberTermEntity.builder()
                .member(createMemberBasicAdminEntityWithUuid())
                .agreedTermsOfUseVersion(MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION)
                .agreedPrivacyPolicyVersion(MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION)
                .agreedCommunityPolicyVersion(MEMBER_TERM_ADMIN_AGREED_COMMUNITY_POLICY_VERSION)
                .build();
    }

    default SiteMemberTermEntity createMemberTermUserEntity() {
        return SiteMemberTermEntity.builder()
                .member(createMemberBasicUserEntity())
                .agreedTermsOfUseVersion(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION)
                .agreedPrivacyPolicyVersion(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION)
                .agreedCommunityPolicyVersion(MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION)
                .build();
    }

    default SiteMemberTermEntity createMemberTermUserEntityWithUuid() {
        return SiteMemberTermEntity.builder()
                .member(createMemberBasicUserEntityWithUuid())
                .agreedTermsOfUseVersion(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION)
                .agreedPrivacyPolicyVersion(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION)
                .agreedCommunityPolicyVersion(MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION)
                .build();
    }
}