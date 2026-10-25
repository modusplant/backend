package kr.modusplant.domains.comment.common.util.domain.vo;

import kr.modusplant.domains.comment.domain.aggregate.Comment;

public interface CommentTestUtils extends PostIdTestUtils, CommentPathTestUtils, ReplyToCommentPathTestUtils,
        AuthorTestUtils, CommentContentTestUtils, CommentStatusTestUtils {

    Comment testValidComment = Comment.create(testPostId, testCommentPath, testReplyToCommentPath, testCommentAuthor, testCommentContent, testCommentStatusValid);
    Comment testInvalidComment = Comment.create(testPostId, testCommentPath, testReplyToCommentPath, testCommentAuthor, testCommentContent, testCommentStatusDeleted);
}
