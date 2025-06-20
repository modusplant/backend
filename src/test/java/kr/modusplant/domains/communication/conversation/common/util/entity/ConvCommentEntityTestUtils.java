package kr.modusplant.domains.communication.conversation.common.util.entity;

import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCommentEntity;

public interface ConvCommentEntityTestUtils extends ConvPostEntityTestUtils {

    default ConvCommentEntity.ConvCommentEntityBuilder createConvCommentEntityBuilder() {
        return ConvCommentEntity.builder()
                .path("1.6.2")
                .content("테스트 댓글 내용");
    }
}