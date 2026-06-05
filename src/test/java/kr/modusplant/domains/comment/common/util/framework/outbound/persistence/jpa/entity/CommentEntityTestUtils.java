package kr.modusplant.domains.comment.common.util.framework.outbound.persistence.jpa.entity;

import kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity.PostEntityTestUtils;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.*;
import static kr.modusplant.domains.comment.framework.outbound.persistence.jpa.entity.CommentEntity.CommentEntityBuilder;
import static kr.modusplant.domains.comment.framework.outbound.persistence.jpa.entity.CommentEntity.builder;

public interface CommentEntityTestUtils extends PostEntityTestUtils {
    default CommentEntityBuilder createCommentEntityBuilder() {
        return builder()
                .path(TEST_COMMENT_PATH)
                .likeCount(TEST_COMMENT_LIKE_COUNT)
                .content(TEST_COMMENT_CONTENT);
    }
}