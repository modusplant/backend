package kr.modusplant.domains.comment.common.util.usecase.response;

import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorPageModel;
import kr.modusplant.domains.comment.usecase.response.CommentPageResponse;

import java.util.List;

import static kr.modusplant.domains.comment.common.util.usecase.model.CommentOfAuthorPageModelTestUtils.testCommentOfAuthorPageModel;

public interface CommentPageResponseTestUtils {
    CommentPageResponse<CommentOfAuthorPageModel> testCommentPageResponseOfAuthorPageModel = new CommentPageResponse<>(
            List.of(testCommentOfAuthorPageModel), 1, 1, 1, 1, false, false
    );

}
