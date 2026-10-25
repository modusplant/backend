package kr.modusplant.domains.post.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.post.domain.exception.AuthorNotFoundException;
import kr.modusplant.domains.post.domain.vo.PostAuthorId;
import kr.modusplant.domains.post.usecase.port.repository.PostAuthorRepository;
import kr.modusplant.shared.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostAuthorRepositoryJpaAdapter implements PostAuthorRepository {
    private final MemberJpaRepository authorJpaRepository;

    @Override
    public Role getRole(PostAuthorId postAuthorId) {
        return authorJpaRepository.findByUuid(postAuthorId.getValue())
                .map(MemberEntity::getRole)
                .orElseThrow(AuthorNotFoundException::new);
    }
}
