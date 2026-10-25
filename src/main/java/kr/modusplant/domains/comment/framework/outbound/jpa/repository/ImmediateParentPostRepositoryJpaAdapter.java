package kr.modusplant.domains.comment.framework.outbound.jpa.repository;

import kr.modusplant.domains.comment.domain.vo.ImmediateParentPostId;
import kr.modusplant.domains.comment.usecase.port.repository.ImmediateParentPostRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ImmediateParentPostRepositoryJpaAdapter implements ImmediateParentPostRepository {
    private final PostJpaRepository postRepository;

    @Override
    public boolean isImmediateParentPostExists(ImmediateParentPostId immediateParentPostId) {
        return postRepository.existsByUlid(immediateParentPostId.getValue());
    }

    @Override
    public boolean isImmediateParentPostPublished(ImmediateParentPostId immediateParentPostId) {
        return postRepository.findByUlid(immediateParentPostId.getValue())
                .map(PostEntity::getIsPublished)
                .map(Boolean.TRUE::equals)
                .orElse(false);
    }
}
