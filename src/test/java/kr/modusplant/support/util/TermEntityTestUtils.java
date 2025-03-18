package kr.modusplant.support.util;

import kr.modusplant.global.persistence.entity.TermEntity;

import static kr.modusplant.global.util.VersionUtils.createVersion;

public interface TermEntityTestUtils {
    default TermEntity createTermsOfUseEntity() {
        return TermEntity.builder()
                .name("이용약관")
                .content("이용약관 내용")
                .version(createVersion(1, 0, 0))
                .build();
    }

    default TermEntity createPrivacyPolicyEntity() {
        return TermEntity.builder()
                .name("개인정보처리방침")
                .content("개인정보처리방침 내용")
                .version(createVersion(1, 0, 2))
                .build();
    }

    default TermEntity createAdInfoReceivingEntity() {
        return TermEntity.builder()
                .name("광고성 정보 수신")
                .content("광고성 정보 수신 내용")
                .version(createVersion(1, 0, 4))
                .build();
    }
}
