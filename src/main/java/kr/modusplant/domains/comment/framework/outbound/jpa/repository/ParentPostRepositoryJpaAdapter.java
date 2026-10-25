package kr.modusplant.domains.comment.framework.outbound.jpa.repository;

import kr.modusplant.domains.comment.domain.vo.ParentPostId;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ParentPostRepositoryJpaAdapter implements ImmediateParentPostRepository {
    private final PostJpaRepository postRepository;

    @Override
    public boolean isParentPostExists(ParentPostId parentPostId) {
        return postRepository.existsByUlid(parentPostId.getValue());
    }

    @Override
    public boolean isParentPostPublished(ParentPostId parentPostId) {
        return postRepository.findByUlid(parentPostId.getValue())
                .map(PostEntity::getIsPublished)
                .map(Boolean.TRUE::equals)
                .orElse(false);
    }
}
