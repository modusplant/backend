package kr.modusplant.domains.comment.common.util.usecase.request;

import kr.modusplant.domains.comment.common.util.domain.vo.CommentContentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.ImmediateParentPostIdTestUtils;
import kr.modusplant.domains.comment.usecase.request.CommentUpdateRequest;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;

public interface CommentUpdateRequestTestUtils extends ImmediateParentPostIdTestUtils, CommentPathTestUtils,
        MemberTestUtils, CommentContentTestUtils {
    CommentUpdateRequest testCommentUpdateRequest = new CommentUpdateRequest(
            testImmediateParentPostId.getValue(), testCommentPath.getValue(), testCommentContent.getValue()
    );
}
