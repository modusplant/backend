package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.MemberTermEntity;

import static kr.modusplant.shared.persistence.common.util.constant.MemberTermConstant.*;

public interface MemberTermEntityTestUtils extends MemberEntityTestUtils {
    default MemberTermEntity createMemberTermAdminEntity() {
        return MemberTermEntity.builder()
                .member(createMemberBasicAdminEntity())
                .agreedTermsOfUseVersion(MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION)
                .agreedPrivacyPolicyVersion(MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION)
                .agreedCommunityPolicyVersion(MEMBER_TERM_ADMIN_AGREED_COMMUNITY_POLICY_VERSION)
                .build();
    }

    default MemberTermEntity createMemberTermAdminEntityWithUuid() {
        return MemberTermEntity.builder()
                .member(createMemberBasicAdminEntityWithUuid())
                .agreedTermsOfUseVersion(MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION)
                .agreedPrivacyPolicyVersion(MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION)
                .agreedCommunityPolicyVersion(MEMBER_TERM_ADMIN_AGREED_COMMUNITY_POLICY_VERSION)
                .build();
    }

    default MemberTermEntity createMemberTermUserEntity() {
        return MemberTermEntity.builder()
                .member(createMemberBasicUserEntity())
                .agreedTermsOfUseVersion(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION)
                .agreedPrivacyPolicyVersion(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION)
                .agreedCommunityPolicyVersion(MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION)
                .build();
    }

    default MemberTermEntity createMemberTermUserEntityWithUuid() {
        return MemberTermEntity.builder()
                .member(createMemberBasicUserEntityWithUuid())
                .agreedTermsOfUseVersion(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION)
                .agreedPrivacyPolicyVersion(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION)
                .agreedCommunityPolicyVersion(MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION)
                .build();
    }
}