package kr.modusplant.domains.comment.support.utils.framework;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.support.utils.domain.CommentTestUtils;

public interface CommentEntityTestUtils extends CommentTestUtils, CommentCompositeKeyTestUtils {

    default CommentEntity createCommentEntity() {
        return CommentEntity.builder()
                .id(testCommentCompositeKey)
                .content(testCommentContent.getContent())
                .isDeleted(false)
                .build();
    }
}
