package kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity;

import kr.modusplant.domains.comment.common.util.framework.outbound.persistence.jpa.entity.CommentEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.CommentLikeEntity;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface CommentLikeEntityTestUtils extends CommentEntityTestUtils, MemberEntityTestUtils {
    default CommentLikeEntity createCommentLikeEntity() {
        return CommentLikeEntity.of(TEST_POST_ULID, TEST_COMM_COMMENT_PATH, MEMBER_BASIC_USER_UUID);
    }
}
