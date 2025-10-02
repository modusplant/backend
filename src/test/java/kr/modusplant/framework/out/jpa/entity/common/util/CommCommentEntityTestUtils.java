package kr.modusplant.framework.out.jpa.entity.common.util;

import static kr.modusplant.framework.out.jpa.entity.CommCommentEntity.CommCommentEntityBuilder;
import static kr.modusplant.framework.out.jpa.entity.CommCommentEntity.builder;
import static kr.modusplant.shared.persistence.common.constant.CommCommentConstant.*;

public interface CommCommentEntityTestUtils extends CommPostEntityTestUtils {
    default CommCommentEntityBuilder createCommCommentEntityBuilder() {
        return builder()
                .path(TEST_COMM_COMMENT_PATH)
                .likeCount(TEST_COMM_COMMENT_LIKE_COUNT)
                .content(TEST_COMM_COMMENT_CONTENT);
    }
}