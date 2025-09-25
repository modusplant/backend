package kr.modusplant.domains.comment.support.utils.adapter;

import kr.modusplant.domains.comment.adapter.response.CommentResponse;
import kr.modusplant.domains.comment.support.utils.domain.CommentContentTestUtils;
import kr.modusplant.domains.comment.support.utils.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.support.utils.domain.PostIdTestUtils;
import kr.modusplant.domains.member.common.utils.domain.vo.MemberNicknameTestUtils;

public interface CommentResponseTestUtils extends
        PostIdTestUtils, CommentPathTestUtils,
        MemberNicknameTestUtils, CommentContentTestUtils, CommentReadModelTestUtils{

    CommentResponse testCommentResponse = new CommentResponse(
            testPostId.getId(), testCommentPath.getPath(), testMemberNickname.getValue(),
            testCommentContent.getContent(), false, testCommentReadModel.createdAt()
    );
}