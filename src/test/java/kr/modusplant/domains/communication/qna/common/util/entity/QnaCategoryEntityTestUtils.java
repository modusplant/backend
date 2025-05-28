package kr.modusplant.domains.communication.qna.common.util.entity;

import kr.modusplant.domains.communication.qna.common.util.domain.QnaCategoryTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;

public interface QnaCategoryEntityTestUtils extends QnaCategoryTestUtils {
    QnaCategoryEntity testQnaCategoryEntity = QnaCategoryEntity.builder()
            .order(testQnaCategory.getOrder())
            .category(testQnaCategory.getCategory())
            .build();
}
