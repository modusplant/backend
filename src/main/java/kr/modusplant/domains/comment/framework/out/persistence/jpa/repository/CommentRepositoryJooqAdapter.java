package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers.CommentJooqRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentRepository;
import kr.modusplant.domains.comment.usecase.response.CommentResponse;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryJooqAdapter implements CommentRepository {
    private final CommentJooqRepository repository;

    @Override
    public List<CommentResponse> findByPost(PostId postId) {
        return repository.findByPostUlid(postId.getId());
    }

    @Override
    public List<CommentResponse> findByAuthor(Author author) {
        return repository.findByAuthMemberUuid(author.getMemberUuid());
    }

    @Override
    public void save(Comment comment) {

    }

    @Override
    public void deleteById(CommCommentId id) {

    }
}
