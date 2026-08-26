package kr.modusplant.domains.comment.adapter.mapper;

import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorReadModel;
import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;
import kr.modusplant.domains.comment.usecase.port.mapper.CommentMapper;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.domains.comment.usecase.response.CommentPageResponse;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapperImpl implements CommentMapper {

    private final AmazonS3Service fileService;

    @Override
    public CommentPageResponse<CommentOfAuthorReadModel> toCommentPageResponseWithOnePlusPage(
            Page<CommentOfAuthorReadModel> paginatedReadModel) {
        return new CommentPageResponse<>(
                paginatedReadModel.getContent(),
                paginatedReadModel.getNumber() + 1,
                paginatedReadModel.getSize(),
                paginatedReadModel.getTotalElements(),
                paginatedReadModel.getTotalPages(),
                paginatedReadModel.hasNext(),
                paginatedReadModel.hasPrevious());
    }

    @Override
    public CommentOfPostResponse toCommentOfPostResponse(CommentOfPostReadModel readModel) {
        return new CommentOfPostResponse(
                readModel.profileImage() == null ? null : fileService.generateS3SrcUrl(readModel.profileImage()),
                readModel.nickname(), readModel.path(), readModel.content(), readModel.likeCount(),
                readModel.isLiked(), readModel.createdAt(), readModel.updatedAt(), readModel.isDeleted()
        );
    }

}
