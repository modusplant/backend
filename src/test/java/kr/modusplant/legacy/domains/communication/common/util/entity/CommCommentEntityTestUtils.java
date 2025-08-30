package kr.modusplant.legacy.domains.communication.common.util.entity;

import kr.modusplant.framework.out.persistence.jpa.entity.CommCommentEntity;

public interface CommCommentEntityTestUtils extends CommPostEntityTestUtils {

    default CommCommentEntity.CommCommentEntityBuilder createCommCommentEntityBuilder() {
        return CommCommentEntity.builder()
                .path("1.6.2")
                .content("테스트 댓글 내용");
    }
}