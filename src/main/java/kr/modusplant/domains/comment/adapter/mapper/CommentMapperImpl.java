package kr.modusplant.domains.comment.adapter.mapper;

import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorReadModel;
import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;
import kr.modusplant.domains.comment.usecase.port.mapper.CommentMapper;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.domains.comment.usecase.response.CommentOffsetPageResponse;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapperImpl implements CommentMapper {

    private final AmazonS3Service fileService;

    @Override
    public CommentOffsetPageResponse<CommentOfAuthorReadModel> toCommentPageResponseWithOnePlusPage(
            Page<CommentOfAuthorReadModel> paginatedReadModel) {
        return new CommentOffsetPageResponse<>(
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
        boolean replyToIsExisted = readModel.replyToIsExisted();
        // 레코드에 답글 대상 경로 값이 존재함에도, 회원 탈퇴 등으로 해당 경로에 대한 댓글 레코드가 존재하지 않으면 삭제된 것으로 간주
        boolean replyToIsDeleted = replyToIsExisted
                ? readModel.replyToIsDeleted()
                : readModel.replyToPath() != null;
        String replyToNickname = replyToIsExisted && !replyToIsDeleted ? readModel.replyToNickname() : null;
        return new CommentOfPostResponse(
                readModel.profileImage() == null ? null : fileService.generateGetPresignedUrl(readModel.profileImage()),
                readModel.nickname(), readModel.path(), readModel.content(), readModel.likeCount(),
                readModel.isLiked(), readModel.createdAt(), readModel.updatedAt(), readModel.isDeleted(),
                readModel.isChosen(), readModel.replyToPath(), replyToIsExisted, replyToNickname, replyToIsDeleted
        );
    }
}
