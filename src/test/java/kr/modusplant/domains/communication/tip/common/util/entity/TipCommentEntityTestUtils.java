package kr.modusplant.domains.communication.tip.common.util.entity;

import kr.modusplant.domains.communication.tip.persistence.entity.TipCommentEntity;

public interface TipCommentEntityTestUtils extends TipPostEntityTestUtils {

    default TipCommentEntity.TipCommentEntityBuilder createTipCommentEntityBuilder() {
        return TipCommentEntity.builder()
                .path("1/6/2/")
                .content("테스트 댓글 내용");
    }
}