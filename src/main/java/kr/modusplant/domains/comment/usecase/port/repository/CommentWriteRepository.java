package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;

public interface CommentWriteRepository {

    void save(Comment comment);

    void update(CommCommentId commentId, CommentContent content);

    void setCommentAsDeleted(CommCommentId id);
}
