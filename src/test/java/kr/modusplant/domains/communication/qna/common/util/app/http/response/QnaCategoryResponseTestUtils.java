package kr.modusplant.domains.communication.qna.common.util.app.http.response;

import kr.modusplant.domains.communication.qna.app.http.response.QnaCategoryResponse;
import kr.modusplant.domains.communication.qna.common.util.domain.QnaCategoryTestUtils;

public interface QnaCategoryResponseTestUtils extends QnaCategoryTestUtils {
    QnaCategoryResponse qnaCategoryResponse = new QnaCategoryResponse(qnaCategory.getOrder(), qnaCategory.getCategory());
}
