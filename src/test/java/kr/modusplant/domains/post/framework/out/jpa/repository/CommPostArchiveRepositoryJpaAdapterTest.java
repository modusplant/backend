package kr.modusplant.domains.post.framework.out.jpa.repository;

import kr.modusplant.domains.post.common.util.framework.out.jpa.entity.PostArchiveEntityTestUtils;
import kr.modusplant.domains.post.common.util.framework.out.jpa.entity.PostEntityTestUtils;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PostArchiveJpaMapper;
import kr.modusplant.framework.jpa.entity.CommPostArchiveEntity;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import kr.modusplant.framework.jpa.repository.CommPostArchiveJpaRepository;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class CommPostArchiveRepositoryJpaAdapterTest implements PostEntityTestUtils, PostArchiveEntityTestUtils {
    private final CommPostArchiveJpaRepository postArchiveJpaRepository = Mockito.mock(CommPostArchiveJpaRepository.class);
    private final PostArchiveJpaMapper postArchiveJpaMapper = Mockito.mock(PostArchiveJpaMapper.class);
    private final CommPostJpaRepository postJpaRepository = Mockito.mock(CommPostJpaRepository.class);
    private final PostArchiveRepositoryJpaAdapter postArchiveRepositoryJpaAdapter = new PostArchiveRepositoryJpaAdapter(
            postArchiveJpaRepository, postArchiveJpaMapper, postJpaRepository
    );

    @Test
    @DisplayName("게시글 아카이브로 게시글 저장")
    void testSave_givenPostId_willSavePostArchive() {
        // given
        CommPostArchiveEntity postArchiveEntity = createPostArchieveEntity();
        CommPostEntity postEntity = createPublishedPostEntityBuilderWithUuid().build();

        given(postJpaRepository.findByUlid(testPostId.getValue())).willReturn(Optional.of(postEntity));
        given(postArchiveJpaMapper.toPostArchiveEntity(postEntity)).willReturn(postArchiveEntity);
        given(postArchiveJpaRepository.save(postArchiveEntity)).willReturn(postArchiveEntity);

        // when
        postArchiveRepositoryJpaAdapter.save(testPostId);

        // then
        verify(postJpaRepository).findByUlid(postEntity.getUlid());
        verify(postArchiveJpaMapper).toPostArchiveEntity(postEntity);
        verify(postArchiveJpaRepository).save(postArchiveEntity);
    }

}