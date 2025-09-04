package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.adapter.model.MemberReadModel;
import kr.modusplant.domains.comment.adapter.repository.CommentAuthorRepository;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper.CommentAuthorMapper;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers.CommentAuthorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentAuthorRepositoryJpaAdapter implements CommentAuthorRepository {
    private final CommentAuthorJpaRepository jpaRepository;
    private final CommentAuthorMapper mapper;

    @Override
    public MemberReadModel findByAuthor(Author author) {
        return mapper.toMemberReadModel(jpaRepository.findByUuid(author.getMemberUuid()));
    }
}
