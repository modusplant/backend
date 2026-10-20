package kr.modusplant.domains.comment.common.util.usecase.response;

import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorReadModel;
import kr.modusplant.domains.comment.usecase.response.CommentOffsetPageResponse;

import java.util.List;

import static kr.modusplant.domains.comment.common.util.usecase.model.CommentOfAuthorPageModelTestUtils.testCommentOfAuthorReadModel;

public interface CommentOffsetPageResponseTestUtils {
    CommentOffsetPageResponse<CommentOfAuthorReadModel> testCommentOffsetPageResponseOfAuthorPageModel = new CommentOffsetPageResponse<>(
            List.of(testCommentOfAuthorReadModel), 1, 1, 1, 1, false, false
    );

}
