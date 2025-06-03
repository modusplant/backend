package kr.modusplant.domains.communication.qna.common.util.domain;

import kr.modusplant.domains.communication.qna.domain.model.QnaCategory;

import java.util.UUID;

public interface QnaCategoryTestUtils {
    QnaCategory testQnaCategory = QnaCategory.builder()
            .category("Q&A 항목")
            .order(1)
            .build();

    QnaCategory testQnaCategoryWithUuid = QnaCategory.builder()
            .uuid(UUID.randomUUID())
            .category(testQnaCategory.getCategory())
            .order(testQnaCategory.getOrder())
            .build();
}
