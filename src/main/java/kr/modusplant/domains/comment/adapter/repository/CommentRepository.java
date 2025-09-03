package kr.modusplant.domains.comment.adapter.repository;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;

public interface CommentRepository {
    void save(Comment comment);

    void deleteById(CommentCompositeKey id);
}
