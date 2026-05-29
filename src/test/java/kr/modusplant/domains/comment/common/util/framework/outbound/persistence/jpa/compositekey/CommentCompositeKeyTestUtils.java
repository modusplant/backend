package kr.modusplant.domains.comment.common.util.framework.outbound.persistence.jpa.compositekey;

import kr.modusplant.domains.comment.common.util.framework.outbound.persistence.jpa.entity.CommentEntityTestUtils;
import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.compositekey.CommentCompositeKey;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface CommentCompositeKeyTestUtils extends CommentEntityTestUtils {
    CommentCompositeKey TEST_COMMENT_ID = CommentCompositeKey.builder()
            .post(TEST_POST_ULID)
            .path(TEST_COMM_COMMENT_PATH)
            .build();
}
