package kr.modusplant.domains.comment.common.util.usecase;

import kr.modusplant.domains.comment.common.util.domain.CommentContentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;

public interface CommentRegisterRequestTestUtils extends PostIdTestUtils, CommentPathTestUtils,
        MemberTestUtils, CommentContentTestUtils {
    CommentRegisterRequest testCommentRegisterRequest = new CommentRegisterRequest(
            testPostId.getId(), testCommentPath.getPath(), testCommentContent.getContent()
    );
}