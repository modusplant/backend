<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/member/common/util/entity/SiteMemberTermEntityTestUtils.java
package kr.modusplant.domains.member.common.util.entity;

import kr.modusplant.domains.member.persistence.entity.SiteMemberTermEntity;

import static kr.modusplant.domains.member.common.util.domain.SiteMemberTermTestUtils.*;
========
package kr.modusplant.api.crud.member.common.util.entity;

import kr.modusplant.api.crud.member.persistence.entity.SiteMemberTermEntity;

import static kr.modusplant.api.crud.member.common.util.domain.SiteMemberTermTestUtils.*;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/test/java/kr/modusplant/api/crud/member/common/util/entity/SiteMemberTermEntityTestUtils.java

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