package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper.CommentJpaMapper;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers.CommentJpaRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentRepository;
import kr.modusplant.domains.comment.usecase.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryJpaAdapter implements CommentRepository {
    private final CommentJpaRepository jpaRepository;
    private final CommentJpaMapper mapper;

    @Override
    public List<CommentResponse> findByPost(PostId postId) {
        return jpaRepository.findByPostUlid(postId.getId());
    }

    @Override
    public List<CommentResponse> findByAuthor(Author author) {
        return jpaRepository.findByAuthMemberUuid(author.getMemberUuid());
    }

    @Override
    public void save(Comment comment) {
        jpaRepository.save(mapper.toCommentEntity(comment));
    }

    @Override
    public void deleteById(CommentCompositeKey id) { jpaRepository.deleteById(id); }

}
