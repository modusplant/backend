package kr.modusplant.domains.comment.support.utils.domain;

import kr.modusplant.domains.comment.domain.aggregate.Comment;

public interface CommentTestUtils extends PostIdTestUtils, CommentPathTestUtils,
        AuthorTestUtils, CommentContentTestUtils, CommentStatusTestUtils {

    Comment testValidComment = Comment.create(testPostId, testCommentPath, testAuthorUuid, testCommentContent, testCommentStatusValid);
    Comment testInvalidComment = Comment.create(testPostId, testCommentPath, testAuthorUuid, testCommentContent, testCommentStatusDeleted);
}
