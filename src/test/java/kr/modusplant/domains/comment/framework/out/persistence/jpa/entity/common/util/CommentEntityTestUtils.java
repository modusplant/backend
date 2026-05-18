package kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.common.util;

import kr.modusplant.domains.post.framework.out.jpa.entity.common.util.PostEntityTestUtils;

import static kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity.CommentEntityBuilder;
import static kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity.builder;
import static kr.modusplant.shared.persistence.common.util.constant.CommentConstant.*;

public interface CommentEntityTestUtils extends PostEntityTestUtils {
    default CommentEntityBuilder createCommentEntityBuilder() {
        return builder()
                .path(TEST_COMM_COMMENT_PATH)
                .likeCount(TEST_COMM_COMMENT_LIKE_COUNT)
                .content(TEST_COMM_COMMENT_CONTENT);
    }
}