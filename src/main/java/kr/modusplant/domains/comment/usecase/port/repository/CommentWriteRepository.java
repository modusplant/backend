package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;

public interface CommentWriteRepository {

    void save(Comment comment);

    void setCommentAsDeleted(CommCommentId id);
}
