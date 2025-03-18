package kr.modusplant.support.util.domain;

import kr.modusplant.global.domain.model.SiteMemberTerm;

import static kr.modusplant.global.util.VersionUtils.createVersion;

public interface SiteMemberTermTestUtils extends SiteMemberTestUtils {
    SiteMemberTerm memberTermAdmin = SiteMemberTerm.builder()
            .agreedTermsOfUseVersion(createVersion(1, 0, 0))
            .agreedPrivacyPolicyVersion(createVersion(1, 0, 2))
            .agreedAdInfoReceivingVersion(createVersion(1, 0, 4))
            .build();

    SiteMemberTerm memberTermUser = SiteMemberTerm.builder()
            .agreedTermsOfUseVersion(createVersion(1, 0, 0))
            .agreedPrivacyPolicyVersion(createVersion(1, 0, 1))
            .agreedAdInfoReceivingVersion(createVersion(1, 0, 3))
            .build();
}