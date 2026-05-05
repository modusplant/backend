package kr.modusplant.domains.comment.common.util.usecase.model;

import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;

import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_PATH;

public interface CommentOfPostReadModelTestUtils {

    CommentOfPostReadModel testCommentOfPostReadModel = new CommentOfPostReadModel(
            MEMBER_PROFILE_BASIC_USER_IMAGE_PATH, MEMBER_BASIC_USER_NICKNAME, TEST_COMM_COMMENT_PATH,
            TEST_COMM_COMMENT_CONTENT, TEST_COMM_COMMENT_LIKE_COUNT, false,
            TEST_COMM_COMMENT_CREATED_AT, TEST_COMM_COMMENT_UPDATED_AT, TEST_COMM_POST_IS_DELETED_FALSE
    );
}
