package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.usecase.response.CommentResponse;

import java.util.List;

public interface CommentRepository {

    List<CommentResponse> findByPost(PostId postId);

    List<CommentResponse> findByAuthor(Author author);

    void save(Comment comment);

    void deleteById(CommentCompositeKey id);
}
