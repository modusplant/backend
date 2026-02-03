package kr.modusplant.domains.comment.adapter.mapper;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;
import kr.modusplant.domains.comment.usecase.port.mapper.CommentMapper;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.framework.aws.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapperImpl implements CommentMapper {

    private final S3FileService fileService;

    @Override
    public Comment toComment(PostId postId, CommentPath path, Author author, CommentContent content) {
        return Comment.create(postId, path, author, content);
    }

    @Override
    public CommentOfPostResponse toCommentOfPostResponse(CommentOfPostReadModel readModel) {
        return new CommentOfPostResponse(
                readModel.profileImage() == null ? null : fileService.generateS3SrcUrl(readModel.profileImage()),
                readModel.nickname(), readModel.path(), readModel.content(), readModel.likeCount(),
                readModel.isLiked(), readModel.createdAt(), readModel.isDeleted()
        );
    }


}
