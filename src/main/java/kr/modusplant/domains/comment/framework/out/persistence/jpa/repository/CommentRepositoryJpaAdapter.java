package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.adapter.model.CommentReadModel;
import kr.modusplant.domains.comment.adapter.repository.CommentRepository;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentMemberEntity;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.PostEntity;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper.CommentJpaMapper;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers.CommentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryJpaAdapter implements CommentRepository {
    private final CommentJpaRepository jpaRepository;
    private final CommentJpaMapper mapper;

    @Override
    public List<CommentReadModel> findByPost(PostId postId) {
        return jpaRepository.findByPostEntity(PostEntity.create(postId.getId()))
                .stream().map(mapper::toCommentReadModel).toList();
    }

    @Override
    public List<CommentReadModel> findByAuthor(Author author) {
        return jpaRepository.findByAuthMember(
                CommentMemberEntity.builder().uuid(author.getMemberUuid()).build()
        ).stream().map(mapper::toCommentReadModel).toList();
    }

    @Override
    public void save(Comment comment) {
        jpaRepository.save(mapper.toCommentEntity(comment));
    }

    @Override
    public void deleteById(CommentCompositeKey id) { jpaRepository.deleteById(id); }

}
