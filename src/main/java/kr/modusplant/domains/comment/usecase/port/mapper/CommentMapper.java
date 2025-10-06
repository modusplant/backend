package kr.modusplant.domains.comment.usecase.port.mapper;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;

public interface CommentMapper {

    Comment toComment(CommentRegisterRequest registerRequest);

}
