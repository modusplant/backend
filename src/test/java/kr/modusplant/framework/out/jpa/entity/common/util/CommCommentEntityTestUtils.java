package kr.modusplant.framework.out.jpa.entity.common.util;

import static kr.modusplant.framework.out.jpa.entity.CommCommentEntity.CommCommentEntityBuilder;
import static kr.modusplant.framework.out.jpa.entity.CommCommentEntity.builder;
import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_CONTENT;
import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;

public interface CommCommentEntityTestUtils extends CommPostEntityTestUtils {
    default CommCommentEntityBuilder createCommCommentEntityBuilder() {
        return builder()
                .path(TEST_COMM_COMMENT_PATH)
                .content(TEST_COMM_COMMENT_CONTENT);
    }
}