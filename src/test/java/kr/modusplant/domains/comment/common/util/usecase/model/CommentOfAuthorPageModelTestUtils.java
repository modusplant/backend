package kr.modusplant.domains.comment.common.util.usecase.model;

import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorPageModel;

import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_CONTENT;
import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_CREATED_AT;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_TITLE;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;

public interface CommentOfAuthorPageModelTestUtils {

    CommentOfAuthorPageModel testCommentOfAuthorPageModel = new CommentOfAuthorPageModel(
            TEST_COMM_COMMENT_CONTENT, TEST_COMM_COMMENT_CREATED_AT,
            TEST_COMM_POST_TITLE, TEST_COMM_POST_ULID, false, 1
    );
}
