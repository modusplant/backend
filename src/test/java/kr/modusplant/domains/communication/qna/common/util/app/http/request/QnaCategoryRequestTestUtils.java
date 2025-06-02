package kr.modusplant.domains.communication.qna.common.util.app.http.request;

import kr.modusplant.domains.communication.qna.app.http.request.QnaCategoryInsertRequest;
import kr.modusplant.domains.communication.qna.common.util.domain.QnaCategoryTestUtils;

public interface QnaCategoryRequestTestUtils extends QnaCategoryTestUtils {
    QnaCategoryInsertRequest testQnaCategoryInsertRequest = new QnaCategoryInsertRequest(testQnaCategory.getCategory(), testQnaCategory.getOrder());
}
