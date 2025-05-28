package kr.modusplant.domains.communication.qna.common.util.app.http.request;

import kr.modusplant.domains.communication.qna.app.http.request.QnaCategoryInsertRequest;
import kr.modusplant.domains.communication.qna.common.util.domain.QnaCategoryTestUtils;

public interface QnaCategoryRequestTestUtils extends QnaCategoryTestUtils {
    QnaCategoryInsertRequest qnaCategoryTestInsertRequest = new QnaCategoryInsertRequest(qnaCategory.getOrder(), qnaCategory.getCategory());
}
