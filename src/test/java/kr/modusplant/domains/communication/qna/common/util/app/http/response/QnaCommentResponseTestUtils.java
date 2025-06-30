package kr.modusplant.domains.communication.qna.common.util.app.http.response;

import kr.modusplant.domains.communication.qna.app.http.response.QnaCommentResponse;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCommentEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCommentEntity;

import java.util.UUID;

public interface QnaCommentResponseTestUtils extends QnaCommentEntityTestUtils {
    default QnaCommentResponse createQnaCommentResponse(String ulid, UUID memberUuid, String nickname) {
        QnaCommentEntity commentEntity = createQnaCommentEntityBuilder().build();
        return new QnaCommentResponse(ulid, commentEntity.getPath(), memberUuid, nickname, commentEntity.getContent());
    }
}
