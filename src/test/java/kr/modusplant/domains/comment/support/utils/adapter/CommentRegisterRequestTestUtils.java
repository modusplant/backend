package kr.modusplant.domains.comment.support.utils.adapter;

import kr.modusplant.domains.comment.support.utils.domain.CommentContentTestUtils;
import kr.modusplant.domains.comment.support.utils.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.support.utils.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import kr.modusplant.domains.member.common.utils.domain.aggregate.MemberTestUtils;

public interface CommentRegisterRequestTestUtils extends PostIdTestUtils, CommentPathTestUtils,
        MemberTestUtils, CommentContentTestUtils {
    CommentRegisterRequest testCommentRegisterRequest = new CommentRegisterRequest(
            testPostId.getId(), testCommentPath.getPath(), testMemberId.getValue(), testCommentContent.getContent()
    );
}