package kr.modusplant.domains.comment.support.utils.adapter;

import kr.modusplant.domains.comment.adapter.request.CommentDeleteRequest;
import kr.modusplant.domains.comment.support.utils.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.support.utils.domain.PostIdTestUtils;

public interface CommentDeleteRequestTestUtils extends PostIdTestUtils, CommentPathTestUtils {
    CommentDeleteRequest testCommentDeleteRequest = new CommentDeleteRequest(
            testPostId.getId(), testCommentPath.getPath()
    );
}