package kr.modusplant.domains.comment.common.util.usecase.response;

import kr.modusplant.domains.comment.usecase.response.CommentCursorPageResponse;

import java.util.List;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.comment.common.util.usecase.response.CommentOfPostResponseTestUtils.testCommentOfPostResponse;

public interface CommentCursorPageResponseTestUtils {
    CommentCursorPageResponse testCommentCursorPageResponse = CommentCursorPageResponse.of(
            List.of(testCommentOfPostResponse), TEST_COMMENT_PATH, false
    );
}
