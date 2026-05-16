package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.CommentLikeEntity;

import static kr.modusplant.shared.persistence.common.util.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_ULID;

public interface CommentLikeEntityTestUtils extends CommentEntityTestUtils, MemberEntityTestUtils {
    default CommentLikeEntity createCommentLikeEntity() {
        return CommentLikeEntity.of(TEST_COMM_POST_ULID, TEST_COMM_COMMENT_PATH, MEMBER_BASIC_USER_UUID);
    }
}
