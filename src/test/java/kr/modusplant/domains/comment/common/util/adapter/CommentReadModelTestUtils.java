package kr.modusplant.domains.comment.common.util.adapter;

import kr.modusplant.domains.comment.common.util.domain.CommentContentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.usecase.model.CommentReadModel;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;

public interface CommentReadModelTestUtils extends PostIdTestUtils, CommentPathTestUtils,
        MemberTestUtils, CommentContentTestUtils {
    CommentReadModel testCommentReadModel = new CommentReadModel(
            testPostId.getId(), testCommentPath.getPath(), testMemberId.getValue().toString(), testMemberId.getValue().toString(),
            testCommentContent.getContent(), false, "2024-11-29T14:30:00+01:00"
    );
}