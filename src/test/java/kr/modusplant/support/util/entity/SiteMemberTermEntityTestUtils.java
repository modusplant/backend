package kr.modusplant.support.util.entity;

import kr.modusplant.global.persistence.entity.SiteMemberTermEntity;

import static kr.modusplant.support.util.domain.SiteMemberTermTestUtils.*;

public interface SiteMemberTermEntityTestUtils extends SiteMemberEntityTestUtils {

    default SiteMemberTermEntity createMemberTermAdminEntity() {
        return SiteMemberTermEntity.builder()
                .member(createMemberBasicAdminEntity())
                .agreedTermsOfUseVersion(memberTermAdmin.getAgreedTermsOfUseVersion())
                .agreedPrivacyPolicyVersion(memberTermAdmin.getAgreedPrivacyPolicyVersion())
                .agreedAdInfoReceivingVersion(memberTermAdmin.getAgreedAdInfoReceivingVersion())
                .build();
    }

    default SiteMemberTermEntity createMemberTermAdminEntityWithUuid() {
        return SiteMemberTermEntity.builder()
                .member(createMemberBasicAdminEntityWithUuid())
                .agreedTermsOfUseVersion(memberTermAdminWithUuid.getAgreedTermsOfUseVersion())
                .agreedPrivacyPolicyVersion(memberTermAdminWithUuid.getAgreedPrivacyPolicyVersion())
                .agreedAdInfoReceivingVersion(memberTermAdminWithUuid.getAgreedAdInfoReceivingVersion())
                .build();
    }

    default SiteMemberTermEntity createMemberTermUserEntity() {
        return SiteMemberTermEntity.builder()
                .member(createMemberBasicUserEntity())
                .agreedTermsOfUseVersion(memberTermUser.getAgreedTermsOfUseVersion())
                .agreedPrivacyPolicyVersion(memberTermUser.getAgreedPrivacyPolicyVersion())
                .agreedAdInfoReceivingVersion(memberTermUser.getAgreedAdInfoReceivingVersion())
                .build();
    }

    default SiteMemberTermEntity createMemberTermUserEntityWithUuid() {
        return SiteMemberTermEntity.builder()
                .member(createMemberBasicUserEntityWithUuid())
                .agreedTermsOfUseVersion(memberTermUserWithUuid.getAgreedTermsOfUseVersion())
                .agreedPrivacyPolicyVersion(memberTermUserWithUuid.getAgreedPrivacyPolicyVersion())
                .agreedAdInfoReceivingVersion(memberTermUserWithUuid.getAgreedAdInfoReceivingVersion())
                .build();
    }
}