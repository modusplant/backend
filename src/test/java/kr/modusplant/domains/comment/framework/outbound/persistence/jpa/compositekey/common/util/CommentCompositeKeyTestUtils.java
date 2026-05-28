package kr.modusplant.domains.comment.framework.outbound.persistence.jpa.compositekey.common.util;

import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.entity.common.util.CommentEntityTestUtils;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface CommentCompositeKeyTestUtils extends CommentEntityTestUtils {
    CommentCompositeKey TEST_COMMENT_ID = CommentCompositeKey.builder()
            .post(TEST_POST_ULID)
            .path(TEST_COMM_COMMENT_PATH)
            .build();
}
