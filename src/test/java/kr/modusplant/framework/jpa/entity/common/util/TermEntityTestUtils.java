package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.TermEntity;

import static kr.modusplant.shared.persistence.common.util.constant.TermConstant.*;

public interface TermEntityTestUtils {
    default TermEntity createTermsOfUseEntity() {
        return TermEntity.builder()
                .name(TERMS_OF_USE_NAME)
                .content(TERMS_OF_USE_CONTENT)
                .version(TERMS_OF_USE_VERSION)
                .build();
    }

    default TermEntity createTermsOfUseEntityWithUuid() {
        return TermEntity.builder()
                .uuid(TERMS_OF_USE_UUID)
                .name(TERMS_OF_USE_NAME)
                .content(TERMS_OF_USE_CONTENT)
                .version(TERMS_OF_USE_VERSION)
                .build();
    }
}
