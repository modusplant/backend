package kr.modusplant.shared.persistence.compositekey.common.util;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.common.util.CommentEntityTestUtils;
import kr.modusplant.shared.persistence.compositekey.CommentCompositeKey;

import static kr.modusplant.shared.persistence.common.util.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_ULID;

public interface CommentCompositeKeyTestUtils extends CommentEntityTestUtils {
    CommentCompositeKey TEST_COMMENT_ID = CommentCompositeKey.builder()
            .post(TEST_COMM_POST_ULID)
            .path(TEST_COMM_COMMENT_PATH)
            .build();
}
