package kr.modusplant.domains.comment.common.util.domain.vo;

import kr.modusplant.domains.comment.domain.aggregate.Comment;

public interface CommentTestUtils extends ImmediateParentPostIdTestUtils, CommentPathTestUtils, ReplyToCommentPathTestUtils,
        CommentAuthorIdTestUtils, CommentContentTestUtils, CommentStatusTestUtils {

    Comment testValidComment = Comment.create(testImmediateParentPostId, testCommentPath, testReplyToCommentPath, testCommentAuthorId, testCommentContent, testCommentStatusValid);
    Comment testInvalidComment = Comment.create(testImmediateParentPostId, testCommentPath, testReplyToCommentPath, testCommentAuthorId, testCommentContent, testCommentStatusDeleted);
}
