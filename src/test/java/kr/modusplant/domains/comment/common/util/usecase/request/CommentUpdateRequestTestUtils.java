package kr.modusplant.domains.comment.common.util.usecase.request;

import kr.modusplant.domains.comment.common.util.domain.vo.CommentContentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.PostIdTestUtils;
import kr.modusplant.domains.comment.usecase.request.CommentUpdateRequest;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;

public interface CommentUpdateRequestTestUtils extends PostIdTestUtils, CommentPathTestUtils,
        MemberTestUtils, CommentContentTestUtils {
    CommentUpdateRequest testCommentUpdateRequest = new CommentUpdateRequest(
            testPostId.getValue(), testCommentPath.getValue(), testCommentContent.getValue()
    );
}
