package kr.modusplant.domains.post.framework.out.jpa.repository;

import kr.modusplant.domains.post.common.util.framework.out.jpa.entity.PostArchiveEntityTestUtils;
import kr.modusplant.domains.post.common.util.framework.out.jpa.entity.PostEntityTestUtils;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PostArchiveJpaMapper;
import kr.modusplant.framework.jpa.entity.PostArchiveEntity;
import kr.modusplant.framework.jpa.entity.PostEntity;
import kr.modusplant.framework.jpa.repository.PostArchiveJpaRepository;
import kr.modusplant.framework.jpa.repository.PostJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class PostArchiveRepositoryJpaAdapterTest implements PostEntityTestUtils, PostArchiveEntityTestUtils {
    private final PostArchiveJpaRepository postArchiveJpaRepository = Mockito.mock(PostArchiveJpaRepository.class);
    private final PostArchiveJpaMapper postArchiveJpaMapper = Mockito.mock(PostArchiveJpaMapper.class);
    private final PostJpaRepository postJpaRepository = Mockito.mock(PostJpaRepository.class);
    private final PostArchiveRepositoryJpaAdapter postArchiveRepositoryJpaAdapter = new PostArchiveRepositoryJpaAdapter(
            postArchiveJpaRepository, postArchiveJpaMapper, postJpaRepository
    );

    @Test
    @DisplayName("게시글 아카이브로 게시글 저장")
    void testSave_givenPostId_willSavePostArchive() {
        // given
        PostArchiveEntity postArchiveEntity = createPostArchiveEntity();
        PostEntity postEntity = createPublishedPostEntityBuilderWithUuid().build();

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