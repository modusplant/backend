package kr.modusplant.domains.communication.qna.common.util.domain;

import kr.modusplant.domains.communication.qna.domain.model.QnaCategory;

public interface QnaCategoryTestUtils {
    QnaCategory qnaCategory = QnaCategory.builder().order(1).category("Q&A 항목").build();
}
