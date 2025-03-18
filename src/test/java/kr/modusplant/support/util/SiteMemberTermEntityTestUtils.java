package kr.modusplant.support.util;

import kr.modusplant.global.persistence.entity.SiteMemberTermEntity;

import static kr.modusplant.global.util.VersionUtils.createVersion;

public interface SiteMemberTermEntityTestUtils extends SiteMemberEntityTestUtils {

    default SiteMemberTermEntity createMemberTermAdminEntity() {
        return SiteMemberTermEntity.builder()
                .member(createMemberBasicAdminEntity())
                .agreedTermsOfUseVersion(createVersion(1, 0, 0))
                .agreedPrivacyPolicyVersion(createVersion(1, 0, 2))
                .agreedAdInfoReceivingVersion(createVersion(1, 0, 4))
                .build();
    }

    default SiteMemberTermEntity createMemberTermUserEntity() {
        return SiteMemberTermEntity.builder()
                .member(createMemberBasicUserEntity())
                .agreedTermsOfUseVersion(createVersion(1, 0, 0))
                .agreedPrivacyPolicyVersion(createVersion(1, 0, 1))
                .agreedAdInfoReceivingVersion(createVersion(1, 0, 3))
                .build();
    }
}