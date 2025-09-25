package kr.modusplant.domains.comment.adapter.mapper.supers;

import kr.modusplant.domains.comment.adapter.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.domain.aggregate.Comment;

public interface CommentMapper {

    Comment toComment(CommentRegisterRequest registerRequest);

}
