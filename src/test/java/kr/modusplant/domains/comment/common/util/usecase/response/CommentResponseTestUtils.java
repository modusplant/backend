package kr.modusplant.domains.comment.common.util.usecase.response;

import kr.modusplant.domains.comment.common.util.domain.CommentContentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.usecase.response.CommentResponse;
import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;

import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_CREATED_AT;

public interface CommentResponseTestUtils extends
        PostIdTestUtils, CommentPathTestUtils,
        NicknameTestUtils, CommentContentTestUtils {

    CommentResponse testCommentResponse = new CommentResponse(
            testPostId.getId(), testCommentPath.getPath(), testNormalUserNickname.getValue(),
            testCommentContent.getContent(), false, TEST_COMM_COMMENT_CREATED_AT.toString()
    );
}