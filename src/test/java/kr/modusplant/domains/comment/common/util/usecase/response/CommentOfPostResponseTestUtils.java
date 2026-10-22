package kr.modusplant.domains.comment.common.util.usecase.response;

import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.*;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_ADMIN_NICKNAME;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_PATH;

public interface CommentOfPostResponseTestUtils {
    CommentOfPostResponse testCommentOfPostResponse = new CommentOfPostResponse(
            MEMBER_PROFILE_BASIC_USER_IMAGE_PATH, MEMBER_BASIC_USER_NICKNAME, TEST_COMMENT_PATH,
            TEST_COMMENT_CONTENT, TEST_COMMENT_LIKE_COUNT, false,
            TEST_COMMENT_CREATED_AT, TEST_COMMENT_EDITED_AT, TEST_COMMENT_IS_DELETED_FALSE,
            TEST_COMMENT_REPLY_TO_PATH, MEMBER_BASIC_ADMIN_NICKNAME, TEST_COMMENT_IS_DELETED_TRUE
    );
}
