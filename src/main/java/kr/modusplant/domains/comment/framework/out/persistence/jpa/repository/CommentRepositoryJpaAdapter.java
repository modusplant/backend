package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.adapter.repository.CommentRepository;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper.CommentJpaMapper;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers.CommentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryJpaAdapter implements CommentRepository {
    private final CommentJpaRepository jpaRepository;
    private final CommentJpaMapper jpaMapper;

    @Override
    public void save(Comment comment) {
        jpaRepository.save(jpaMapper.toCommentEntity(comment));
    }
}
