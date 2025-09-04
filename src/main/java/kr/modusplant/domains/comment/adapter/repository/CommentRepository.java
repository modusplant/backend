package kr.modusplant.domains.comment.adapter.repository;

import kr.modusplant.domains.comment.adapter.model.CommentReadModel;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;

import java.util.List;

public interface CommentRepository {

    List<CommentReadModel> findByPost(PostId postId);

    List<CommentReadModel> findByAuthor(Author author);

    void save(Comment comment);

    void deleteById(CommentCompositeKey id);
}
