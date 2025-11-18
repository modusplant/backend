package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.shared.persistence.compositekey.CommCommentId;

import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;

public interface CommCommentIdTestUtils extends CommCommentEntityTestUtils {
    CommCommentId testCommCommentId = CommCommentId.builder()
            .postUlid(TEST_COMM_POST_ULID)
            .path(TEST_COMM_COMMENT_PATH)
            .build();
}
