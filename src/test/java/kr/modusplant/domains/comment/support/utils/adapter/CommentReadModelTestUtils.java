package kr.modusplant.domains.comment.support.utils.adapter;

import kr.modusplant.domains.comment.adapter.model.CommentReadModel;
import kr.modusplant.domains.comment.support.utils.domain.CommentContentTestUtils;
import kr.modusplant.domains.comment.support.utils.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.support.utils.domain.PostIdTestUtils;
import kr.modusplant.domains.member.test.utils.domain.MemberTestUtils;

import java.time.LocalDateTime;

public interface CommentReadModelTestUtils extends PostIdTestUtils, CommentPathTestUtils,
        MemberTestUtils, CommentContentTestUtils {
    CommentReadModel testCommentReadModel = new CommentReadModel(
            testPostId.getId(), testCommentPath.getPath(), testMemberId.getValue().toString(), testMemberId.getValue().toString(),
            testCommentContent.getContent(), false, "2024-11-29T14:30:00+01:00"
    );
}