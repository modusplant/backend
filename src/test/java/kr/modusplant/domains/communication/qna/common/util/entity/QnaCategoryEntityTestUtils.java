package kr.modusplant.domains.communication.qna.common.util.entity;

import kr.modusplant.domains.communication.qna.common.util.domain.QnaCategoryTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;

public interface QnaCategoryEntityTestUtils extends QnaCategoryTestUtils {
    default QnaCategoryEntity createTestQnaCategoryEntity() {
        return QnaCategoryEntity.builder()
                .category(testQnaCategory.getCategory())
                .order(testQnaCategory.getOrder())
                .build();
    }

    default QnaCategoryEntity createTestQnaCategoryEntityWithUuid() {
        return QnaCategoryEntity.builder()
                .uuid(testQnaCategoryWithUuid.getUuid())
                .category(testQnaCategory.getCategory())
                .order(testQnaCategory.getOrder())
                .build();
    }
}
