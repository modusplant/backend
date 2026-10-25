package kr.modusplant.domains.comment.common.util.usecase.request;

import kr.modusplant.domains.comment.common.util.domain.vo.CommentContentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.ImmediateParentPostIdTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.ReplyToCommentPathTestUtils;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;

public interface CommentRegisterRequestTestUtils extends ImmediateParentPostIdTestUtils, CommentPathTestUtils, ReplyToCommentPathTestUtils,
        MemberTestUtils, CommentContentTestUtils {
    CommentRegisterRequest testCommentRegisterRequest = new CommentRegisterRequest(
            testReplyToCommentPath.getValue(), testImmediateParentPostId.getValue(), testCommentPath.getValue(), testCommentContent.getValue()
    );
}