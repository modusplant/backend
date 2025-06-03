package kr.modusplant.domains.communication.qna.common.util.app.http.request;

import kr.modusplant.domains.communication.qna.app.http.request.QnaCommentInsertRequest;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCommentEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCommentEntity;

import java.util.UUID;

public interface QnaCommentInsertRequestTestUtils extends QnaCommentEntityTestUtils {
    default QnaCommentInsertRequest createQnaCommentInsertRequest(String postUlid, UUID createMemberUuid) {
        QnaCommentEntity commentEntity = createQnaCommentEntityBuilder().build();

        return new QnaCommentInsertRequest(postUlid, commentEntity.getPath(),
                createMemberUuid, commentEntity.getContent());
    }
}
