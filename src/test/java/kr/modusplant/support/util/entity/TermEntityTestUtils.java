package kr.modusplant.support.util.entity;

import kr.modusplant.global.persistence.entity.TermEntity;
import kr.modusplant.support.util.domain.TermTestUtils;

public interface TermEntityTestUtils extends TermTestUtils {
    default TermEntity createTermsOfUseEntity() {
        return TermEntity.builder()
                .name(termsOfUse.getName())
                .content(termsOfUse.getContent())
                .version(termsOfUse.getVersion())
                .build();
    }

    default TermEntity createPrivacyPolicyEntity() {
        return TermEntity.builder()
                .name(privacyPolicy.getName())
                .content(privacyPolicy.getContent())
                .version(privacyPolicy.getVersion())
                .build();
    }

    default TermEntity createAdInfoReceivingEntity() {
        return TermEntity.builder()
                .name(adInfoReceiving.getName())
                .content(adInfoReceiving.getContent())
                .version(adInfoReceiving.getVersion())
                .build();
    }
}
