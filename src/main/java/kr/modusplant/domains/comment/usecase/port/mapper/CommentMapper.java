package kr.modusplant.domains.comment.usecase.port.mapper;

import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorReadModel;
import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.domains.comment.usecase.response.CommentOffsetPageResponse;
import org.springframework.data.domain.Page;

public interface CommentMapper {
    CommentOffsetPageResponse<CommentOfAuthorReadModel> toCommentPageResponseWithOnePlusPage(Page<CommentOfAuthorReadModel> paginatedReadModel);

    CommentOfPostResponse toCommentOfPostResponse(CommentOfPostReadModel readModel);
}
