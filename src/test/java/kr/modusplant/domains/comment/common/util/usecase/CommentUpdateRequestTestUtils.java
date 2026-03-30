package kr.modusplant.domains.comment.common.util.usecase;

import kr.modusplant.domains.comment.common.util.domain.CommentContentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.usecase.request.CommentUpdateRequest;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;

public interface CommentUpdateRequestTestUtils extends PostIdTestUtils, CommentPathTestUtils,
        MemberTestUtils, CommentContentTestUtils {
    CommentUpdateRequest testCommentUpdateRequest = new CommentUpdateRequest(
            testPostId.getId(), testCommentPath.getPath(), testCommentContent.getContent()
    );
}
