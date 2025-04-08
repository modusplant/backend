package kr.modusplant.api.crud.member.common.util.domain;

import kr.modusplant.api.crud.member.domain.model.SiteMemberTerm;

import static kr.modusplant.global.util.VersionUtils.createVersion;

public interface SiteMemberTermTestUtils extends SiteMemberTestUtils {
    SiteMemberTerm memberTermAdmin = SiteMemberTerm.builder()
            .agreedTermsOfUseVersion(createVersion(1, 0, 0))
            .agreedPrivacyPolicyVersion(createVersion(1, 0, 2))
            .agreedAdInfoReceivingVersion(createVersion(1, 0, 4))
            .build();

    SiteMemberTerm memberTermAdminWithUuid = SiteMemberTerm.builder()
            .uuid(memberBasicAdminWithUuid.getUuid())
            .agreedTermsOfUseVersion(memberTermAdmin.getAgreedTermsOfUseVersion())
            .agreedPrivacyPolicyVersion(memberTermAdmin.getAgreedPrivacyPolicyVersion())
            .agreedAdInfoReceivingVersion(memberTermAdmin.getAgreedAdInfoReceivingVersion())
            .build();

    SiteMemberTerm memberTermUser = SiteMemberTerm.builder()
            .agreedTermsOfUseVersion(createVersion(1, 0, 0))
            .agreedPrivacyPolicyVersion(createVersion(1, 0, 1))
            .agreedAdInfoReceivingVersion(createVersion(1, 0, 3))
            .build();

    SiteMemberTerm memberTermUserWithUuid = SiteMemberTerm.builder()
            .uuid(memberBasicUserWithUuid.getUuid())
            .agreedTermsOfUseVersion(memberTermUser.getAgreedTermsOfUseVersion())
            .agreedPrivacyPolicyVersion(memberTermUser.getAgreedPrivacyPolicyVersion())
            .agreedAdInfoReceivingVersion(memberTermUser.getAgreedAdInfoReceivingVersion())
            .build();
}