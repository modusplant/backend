package kr.modusplant.domains.comment.common.util.domain.vo;

import kr.modusplant.domains.comment.domain.aggregate.Comment;

public interface CommentTestUtils extends ImmediateParentPostIdTestUtils, CommentPathTestUtils, ReplyToCommentPathTestUtils,
        AuthorTestUtils, CommentContentTestUtils, CommentStatusTestUtils {

    Comment testValidComment = Comment.create(testImmediateParentPostId, testCommentPath, testReplyToCommentPath, testCommentAuthor, testCommentContent, testCommentStatusValid);
    Comment testInvalidComment = Comment.create(testImmediateParentPostId, testCommentPath, testReplyToCommentPath, testCommentAuthor, testCommentContent, testCommentStatusDeleted);
}
