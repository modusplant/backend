package kr.modusplant.domains.communication.qna.common.util.app.http.request;

import kr.modusplant.domains.communication.qna.app.http.request.QnaCommentInsertRequest;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCommentEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCommentEntity;

public interface QnaCommentInsertRequestTestUtils extends QnaCommentEntityTestUtils {
    default QnaCommentInsertRequest createQnaCommentInsertRequest(String postUlid) {
        QnaCommentEntity commentEntity = createQnaCommentEntityBuilder().build();

        return new QnaCommentInsertRequest(postUlid, commentEntity.getPath(), commentEntity.getContent());
    }
}
