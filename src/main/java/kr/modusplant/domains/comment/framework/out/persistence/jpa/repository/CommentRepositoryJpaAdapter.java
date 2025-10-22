package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper.CommentJpaMapper;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers.CommentJpaRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentWriteRepository;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryJpaAdapter implements CommentWriteRepository {
    private final CommentJpaRepository jpaRepository;
    private final CommentJpaMapper mapper;

    @Override
    public void save(Comment comment) {
        jpaRepository.save(mapper.toCommCommentEntity(comment));
    }

    @Override
    public void deleteById(CommCommentId id) { jpaRepository.deleteById(id); }

}
