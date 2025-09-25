package kr.modusplant.domains.comment.usecase.port.mapper;

import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.domain.aggregate.Comment;

public interface CommentMapper {

    Comment toComment(CommentRegisterRequest registerRequest);

}
