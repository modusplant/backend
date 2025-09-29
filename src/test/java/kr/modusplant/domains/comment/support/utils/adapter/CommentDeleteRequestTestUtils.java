package kr.modusplant.domains.comment.support.utils.adapter;

import kr.modusplant.domains.comment.support.utils.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.support.utils.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.usecase.request.CommentDeleteRequest;

public interface CommentDeleteRequestTestUtils extends PostIdTestUtils, CommentPathTestUtils {
    CommentDeleteRequest testCommentDeleteRequest = new CommentDeleteRequest(
            testPostId.getId(), testCommentPath.getPath()
    );
}