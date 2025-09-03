package kr.modusplant.domains.comment.adapter.repository;

import kr.modusplant.domains.comment.domain.aggregate.Comment;

public interface CommentRepository {
    void save(Comment comment);
}
