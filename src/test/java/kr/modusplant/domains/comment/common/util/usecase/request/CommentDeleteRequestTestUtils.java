package kr.modusplant.domains.comment.common.util.usecase.request;

import kr.modusplant.domains.comment.common.util.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.usecase.request.CommentDeleteRequest;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;

public interface CommentDeleteRequestTestUtils extends PostIdTestUtils, CommentPathTestUtils,
        MemberTestUtils {
    CommentDeleteRequest testCommentDeleteRequest = new CommentDeleteRequest(
            testPostId.getValue(), testCommentPath.getValue()
    );
}
