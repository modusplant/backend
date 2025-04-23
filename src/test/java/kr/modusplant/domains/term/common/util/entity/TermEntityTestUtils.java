package kr.modusplant.domains.term.common.util.entity;

import kr.modusplant.domains.term.common.util.domain.TermTestUtils;
import kr.modusplant.domains.term.persistence.entity.TermEntity;

public interface TermEntityTestUtils extends TermTestUtils {
    default TermEntity createTermsOfUseEntity() {
        return TermEntity.builder()
                .name(termsOfUse.getName())
                .content(termsOfUse.getContent())
                .version(termsOfUse.getVersion())
                .build();
    }

    default TermEntity createTermsOfUseEntityWithUuid() {
        return TermEntity.builder()
                .uuid(termsOfUseWithUuid.getUuid())
                .name(termsOfUseWithUuid.getName())
                .content(termsOfUseWithUuid.getContent())
                .version(termsOfUseWithUuid.getVersion())
                .build();
    }

    default TermEntity createPrivacyPolicyEntity() {
        return TermEntity.builder()
                .name(privacyPolicy.getName())
                .content(privacyPolicy.getContent())
                .version(privacyPolicy.getVersion())
                .build();
    }

    default TermEntity createPrivacyPolicyEntityWithUuid() {
        return TermEntity.builder()
                .uuid(privacyPolicyWithUuid.getUuid())
                .name(privacyPolicyWithUuid.getName())
                .content(privacyPolicyWithUuid.getContent())
                .version(privacyPolicyWithUuid.getVersion())
                .build();
    }

    default TermEntity createAdInfoReceivingEntity() {
        return TermEntity.builder()
                .name(adInfoReceiving.getName())
                .content(adInfoReceiving.getContent())
                .version(adInfoReceiving.getVersion())
                .build();
    }

    default TermEntity createAdInfoReceivingEntityWithUuid() {
        return TermEntity.builder()
                .uuid(adInfoReceivingWithUuid.getUuid())
                .name(adInfoReceivingWithUuid.getName())
                .content(adInfoReceivingWithUuid.getContent())
                .version(adInfoReceivingWithUuid.getVersion())
                .build();
    }
}
