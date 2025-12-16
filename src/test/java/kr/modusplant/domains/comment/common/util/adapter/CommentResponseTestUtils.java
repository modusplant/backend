package kr.modusplant.domains.comment.common.util.adapter;

import kr.modusplant.domains.comment.common.util.domain.CommentContentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.usecase.response.CommentResponse;
import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;

public interface CommentResponseTestUtils extends
        PostIdTestUtils, CommentPathTestUtils,
        NicknameTestUtils, CommentContentTestUtils, CommentReadModelTestUtils{

    CommentResponse testCommentResponse = new CommentResponse(
            testPostId.getId(), testCommentPath.getPath(), testNormalUserNickname.getValue(),
            testCommentContent.getContent(), false, testCommentReadModel.createdAt()
    );
}