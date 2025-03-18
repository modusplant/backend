package kr.modusplant.support.util.entity;

import kr.modusplant.global.persistence.entity.SiteMemberTermEntity;

import static kr.modusplant.support.util.domain.SiteMemberTermTestUtils.memberTermAdmin;
import static kr.modusplant.support.util.domain.SiteMemberTermTestUtils.memberTermUser;

public interface SiteMemberTermEntityTestUtils extends SiteMemberEntityTestUtils {

    default SiteMemberTermEntity createMemberTermAdminEntity() {
        return SiteMemberTermEntity.builder()
                .member(createMemberBasicAdminEntity())
                .agreedTermsOfUseVersion(memberTermAdmin.getAgreedTermsOfUseVersion())
                .agreedPrivacyPolicyVersion(memberTermAdmin.getAgreedPrivacyPolicyVersion())
                .agreedAdInfoReceivingVersion(memberTermAdmin.getAgreedAdInfoReceivingVersion())
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
}