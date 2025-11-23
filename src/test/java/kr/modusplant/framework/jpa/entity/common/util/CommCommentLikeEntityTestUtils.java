package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.CommCommentLikeEntity;

import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface CommCommentLikeEntityTestUtils extends CommCommentEntityTestUtils, SiteMemberEntityTestUtils {
    default CommCommentLikeEntity createCommCommentLikeEntity() {
        return CommCommentLikeEntity.of(TEST_COMM_POST_ULID, TEST_COMM_COMMENT_PATH, MEMBER_BASIC_USER_UUID);
    }
}
