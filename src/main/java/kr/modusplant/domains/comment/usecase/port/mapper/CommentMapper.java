package kr.modusplant.domains.comment.usecase.port.mapper;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;

public interface CommentMapper {

    Comment toComment(PostId postId, CommentPath path, Author author, CommentContent content);

    CommentOfPostResponse toCommentOfPostResponse(CommentOfPostReadModel readModel);

}
