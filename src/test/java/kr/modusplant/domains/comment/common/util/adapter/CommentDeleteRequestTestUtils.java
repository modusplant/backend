package kr.modusplant.domains.comment.common.util.adapter;

import kr.modusplant.domains.comment.common.util.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.usecase.request.CommentDeleteRequest;

public interface CommentDeleteRequestTestUtils extends PostIdTestUtils, CommentPathTestUtils {
    CommentDeleteRequest testCommentDeleteRequest = new CommentDeleteRequest(
            testPostId.getId(), testCommentPath.getPath()
    );
}