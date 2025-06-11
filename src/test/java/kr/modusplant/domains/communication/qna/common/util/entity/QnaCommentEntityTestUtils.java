package kr.modusplant.domains.communication.qna.common.util.entity;

import kr.modusplant.domains.communication.qna.persistence.entity.QnaCommentEntity;

public interface QnaCommentEntityTestUtils extends QnaPostEntityTestUtils {

    default QnaCommentEntity.QnaCommentEntityBuilder createQnaCommentEntityBuilder() {
        return QnaCommentEntity.builder()
                .path("1/6/2/")
                .content("테스트 댓글 내용");
    }
}