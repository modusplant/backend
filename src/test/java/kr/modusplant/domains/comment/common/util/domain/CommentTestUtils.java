package kr.modusplant.domains.comment.common.util.domain;

import kr.modusplant.domains.comment.domain.aggregate.Comment;

public interface CommentTestUtils extends PostIdTestUtils, CommentPathTestUtils,
        AuthorTestUtils, CommentContentTestUtils, CommentStatusTestUtils {

    Comment testValidComment = Comment.create(testPostId, testCommentPath, testAuthor, testCommentContent, testCommentStatusValid);
    Comment testInvalidComment = Comment.create(testPostId, testCommentPath, testAuthor, testCommentContent, testCommentStatusDeleted);
}
