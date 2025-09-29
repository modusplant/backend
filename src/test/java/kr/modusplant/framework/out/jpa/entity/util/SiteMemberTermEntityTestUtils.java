package kr.modusplant.framework.out.jpa.entity.util;

import kr.modusplant.framework.out.jpa.entity.SiteMemberTermEntity;

import static kr.modusplant.framework.out.jpa.entity.constant.SiteMemberTermEntityConstant.*;

public interface SiteMemberTermEntityTestUtils extends SiteMemberEntityTestUtils {
    default SiteMemberTermEntity createMemberTermAdminEntity() {
        return SiteMemberTermEntity.builder()
                .member(createMemberBasicAdminEntity())
                .agreedTermsOfUseVersion(MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION)
                .agreedPrivacyPolicyVersion(MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION)
                .agreedAdInfoReceivingVersion(MEMBER_TERM_ADMIN_AGREED_AD_INFO_RECEIVING_VERSION)
                .build();
    }

    default SiteMemberTermEntity createMemberTermAdminEntityWithUuid() {
        return SiteMemberTermEntity.builder()
                .member(createMemberBasicAdminEntityWithUuid())
                .agreedTermsOfUseVersion(MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION)
                .agreedPrivacyPolicyVersion(MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION)
                .agreedAdInfoReceivingVersion(MEMBER_TERM_ADMIN_AGREED_AD_INFO_RECEIVING_VERSION)
                .build();
    }

    default SiteMemberTermEntity createMemberTermUserEntity() {
        return SiteMemberTermEntity.builder()
                .member(createMemberBasicUserEntity())
                .agreedTermsOfUseVersion(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION)
                .agreedPrivacyPolicyVersion(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION)
                .agreedAdInfoReceivingVersion(MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION)
                .build();
    }

    default SiteMemberTermEntity createMemberTermUserEntityWithUuid() {
        return SiteMemberTermEntity.builder()
                .member(createMemberBasicUserEntityWithUuid())
                .agreedTermsOfUseVersion(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION)
                .agreedPrivacyPolicyVersion(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION)
                .agreedAdInfoReceivingVersion(MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION)
                .build();
    }
}