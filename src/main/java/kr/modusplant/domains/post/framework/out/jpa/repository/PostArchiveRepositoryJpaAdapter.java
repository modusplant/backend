package kr.modusplant.domains.post.framework.out.jpa.repository;

import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PostArchiveJpaMapper;
import kr.modusplant.domains.post.framework.out.jpa.repository.supers.PostArchiveJpaRepository;
import kr.modusplant.domains.post.framework.out.jpa.repository.supers.PostJpaRepository;
import kr.modusplant.domains.post.usecase.port.repository.PostArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostArchiveRepositoryJpaAdapter implements PostArchiveRepository {
    private final PostArchiveJpaRepository postArchiveJpaRepository;
    private final PostArchiveJpaMapper postArchiveJpaMapper;
    private final PostJpaRepository postJpaRepository;

    @Override
    public void save(PostId postId) {
        postJpaRepository.findByUlid(postId.getValue())
                .ifPresent(postEntity -> {
                    postArchiveJpaRepository.save(postArchiveJpaMapper.toPostArchiveEntity(postEntity));
                });
    }
}
