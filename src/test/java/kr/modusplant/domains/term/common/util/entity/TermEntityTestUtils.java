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
                .name(termsOfUse.getName())
                .content(termsOfUse.getContent())
                .version(termsOfUse.getVersion())
                .build();
    }
}
