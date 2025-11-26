package kr.modusplant.domains.post.framework.out.jpa.repository;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.common.util.framework.out.jpa.entity.PostEntityTestUtils;
import kr.modusplant.domains.post.common.util.usecase.model.PostReadModelTestUtils;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PostJpaMapper;
import kr.modusplant.domains.post.framework.out.jpa.repository.supers.PostJpaRepository;
import kr.modusplant.domains.post.framework.out.redis.PostViewCountRedisRepository;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import kr.modusplant.framework.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.common.util.CommPrimaryCategoryEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.jpa.repository.CommPrimaryCategoryJpaRepository;
import kr.modusplant.framework.jpa.repository.CommSecondaryCategoryJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PostRepositoryJpaAdapterTest implements PostTestUtils, PostEntityTestUtils, SiteMemberEntityTestUtils, CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils, PostReadModelTestUtils {
    private final PostJpaMapper postJpaMapper = Mockito.mock(PostJpaMapper.class);
    private final PostJpaRepository postJpaRepository = Mockito.mock(PostJpaRepository.class);
    private final SiteMemberJpaRepository authorJpaRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final CommPrimaryCategoryJpaRepository primaryCategoryJpaRepository = Mockito.mock(CommPrimaryCategoryJpaRepository.class);
    private final CommSecondaryCategoryJpaRepository secondaryCategoryJpaRepository = Mockito.mock(CommSecondaryCategoryJpaRepository.class);
    private final PostViewCountRedisRepository postViewCountRedisRepository = Mockito.mock(PostViewCountRedisRepository.class);
    private final PostRepositoryJpaAdapter postRepositoryJpaAdapter = new PostRepositoryJpaAdapter(
            postJpaMapper, postJpaRepository, authorJpaRepository, primaryCategoryJpaRepository, secondaryCategoryJpaRepository, postViewCountRedisRepository
    );

    @Test
    @DisplayName("게시글 저장하기")
    void testSave_givenPost_willReturnPostDetailReadModel() {
        // given
        Post post = createPublishedPost();
        CommPostEntity postEntity = createPublishedPostEntityBuilderWithUuid().build();
        SiteMemberEntity memberEntity = createMemberBasicUserEntity().builder().uuid(post.getAuthorId().getValue()).build();
        CommPrimaryCategoryEntity primaryCategoryEntity = createCommPrimaryCategoryEntity().builder().uuid(post.getPrimaryCategoryId().getValue()).build();
        CommSecondaryCategoryEntity secondaryCategoryEntity = createCommSecondaryCategoryEntityBuilder().uuid(post.getSecondaryCategoryId().getValue()).build();
        long viewCount = 0L;

        given(authorJpaRepository.findByUuid(post.getAuthorId().getValue())).willReturn(Optional.of(memberEntity));
        given(authorJpaRepository.findByUuid(post.getCreateAuthorId().getValue())).willReturn(Optional.of(memberEntity));
        given(primaryCategoryJpaRepository.findByUuid(post.getPrimaryCategoryId().getValue())).willReturn(Optional.of(primaryCategoryEntity));
        given(secondaryCategoryJpaRepository.findByUuid(post.getSecondaryCategoryId().getValue())).willReturn(Optional.of(secondaryCategoryEntity));
        given(postViewCountRedisRepository.read(post.getPostId())).willReturn(viewCount);
        given(postJpaMapper.toPostEntity(post, memberEntity, memberEntity, primaryCategoryEntity, secondaryCategoryEntity, viewCount)).willReturn(postEntity);
        given(postJpaRepository.save(postEntity)).willReturn(postEntity);

        // when
        postRepositoryJpaAdapter.save(post);

        // then
        verify(authorJpaRepository, times(2)).findByUuid(any(UUID.class));
        verify(primaryCategoryJpaRepository).findByUuid(post.getPrimaryCategoryId().getValue());
        verify(secondaryCategoryJpaRepository).findByUuid(post.getSecondaryCategoryId().getValue());
        verify(postViewCountRedisRepository).read(any(PostId.class));
        verify(postJpaMapper).toPostEntity(post, memberEntity, memberEntity, primaryCategoryEntity, secondaryCategoryEntity, viewCount);
        verify(postJpaRepository).save(postEntity);
    }

    @Test
    @DisplayName("게시글 삭제하기")
    void testDelete_givenPost_willDeletePost() {
        // given
        Post post = createPublishedPost();

        // when
        postRepositoryJpaAdapter.delete(post);

        // then
        verify(postJpaRepository).deleteByUlid(testPostId.getValue());
    }

    @Test
    @DisplayName("postid로 Post 가져오기")
    void testGetPostByUlid_givenPostId_willReturnPost() {
        // given
        CommPostEntity postEntity = createDraftPostEntityBuilder().ulid(testPostId.getValue()).build();
        Post expectedPost = createPublishedPost();

        given(postJpaRepository.findByUlid(testPostId.getValue())).willReturn(Optional.of(postEntity));
        given(postJpaMapper.toPost(postEntity)).willReturn(expectedPost);

        // when
        Optional<Post> result = postRepositoryJpaAdapter.getPostByUlid(testPostId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedPost);
        verify(postJpaRepository).findByUlid(testPostId.getValue());
        verify(postJpaMapper).toPost(postEntity);
    }

    @Test
    @DisplayName("postid로 조회수 가져오기")
    void testGetViewCountByUlid_givenPostId_willReturnViewCount() {
        // given
        CommPostEntity postEntity = createDraftPostEntityBuilder().ulid(testPostId.getValue()).build();
        given(postJpaRepository.findByUlid(testPostId.getValue())).willReturn(Optional.of(postEntity));

        // when
        Long result = postRepositoryJpaAdapter.getViewCountByUlid(testPostId);

        // then
        assertThat(result).isEqualTo(postEntity.getViewCount());
        verify(postJpaRepository).findByUlid(testPostId.getValue());
    }

    @Test
    @DisplayName("조회수 업데이트 하기")
    void testUpdateViewCount_givenPostIdAndViewCount_willReturnViewCount() {
        // given
        Long newViewCount = 10L;
        int expectedAffectedRows = 1;
        given(postJpaRepository.updateViewCount(testPostId.getValue(), newViewCount)).willReturn(expectedAffectedRows);

        // when
        int result = postRepositoryJpaAdapter.updateViewCount(testPostId,newViewCount);

        // then
        assertThat(result).isEqualTo(expectedAffectedRows);
        verify(postJpaRepository).updateViewCount(testPostId.getValue(), newViewCount);
    }

}