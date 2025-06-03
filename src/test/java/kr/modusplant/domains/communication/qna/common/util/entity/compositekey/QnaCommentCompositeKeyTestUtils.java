package kr.modusplant.domains.communication.qna.common.util.entity.compositekey;

import kr.modusplant.domains.communication.qna.common.util.entity.QnaCommentEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCommentEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.compositekey.QnaCommentCompositeKey;

public interface QnaCommentCompositeKeyTestUtils extends QnaCommentEntityTestUtils {
    default QnaCommentCompositeKey createQnaCommentCompositeKey(String postUlid) {
        QnaCommentEntity commentEntity = createQnaCommentEntityBuilder().build();

        return new QnaCommentCompositeKey(postUlid,commentEntity.getContent());
    }
}
