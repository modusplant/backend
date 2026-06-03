package kr.modusplant.domains.comment.common.util.usecase.model;

import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.*;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_PATH;

public interface CommentOfPostReadModelTestUtils {

    CommentOfPostReadModel testCommentOfPostReadModel = new CommentOfPostReadModel(
            MEMBER_PROFILE_BASIC_USER_IMAGE_PATH, MEMBER_BASIC_USER_NICKNAME, TEST_COMMENT_PATH,
            TEST_COMMENT_CONTENT, TEST_COMMENT_LIKE_COUNT, false,
            TEST_COMMENT_CREATED_AT, TEST_COMMENT_EDITED_AT, TEST_POST_IS_DELETED_FALSE
    );
}
