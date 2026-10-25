package kr.modusplant.domains.comment.common.util.usecase.response;

import kr.modusplant.domains.comment.common.util.domain.vo.CommentContentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.ImmediateParentPostIdTestUtils;
import kr.modusplant.domains.comment.usecase.response.CommentResponse;
import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_CREATED_AT;

public interface CommentResponseTestUtils extends
        ImmediateParentPostIdTestUtils, CommentPathTestUtils,
        NicknameTestUtils, CommentContentTestUtils {

    CommentResponse testCommentResponse = new CommentResponse(
            testImmediateParentPostId.getValue(), testCommentPath.getValue(), testNormalUserNickname.getValue(),
            testCommentContent.getValue(), false, TEST_COMMENT_CREATED_AT.toString()
    );
}