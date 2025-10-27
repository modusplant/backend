package kr.modusplant.domains.post.framework.out.jpa.repository;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.common.util.framework.out.jpa.entity.PostEntityTestUtils;
import kr.modusplant.domains.post.common.util.usecase.model.PostReadModelTestUtils;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.AuthorId;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.domain.vo.PrimaryCategoryId;
import kr.modusplant.domains.post.domain.vo.SecondaryCategoryId;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PostJpaMapper;
import kr.modusplant.domains.post.framework.out.jpa.repository.supers.PostJpaRepository;
import kr.modusplant.domains.post.framework.out.redis.PostViewCountRedisRepository;
import kr.modusplant.domains.post.usecase.model.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.model.PostSummaryReadModel;
import kr.modusplant.framework.out.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.common.util.CommPrimaryCategoryEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.CommPrimaryCategoryJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommSecondaryCategoryJpaRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
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
    @DisplayName("")
    void testSave_givenPost_willReturnPostDetailReadModel() {
        // given
        Post post = createPublishedPost();
        PostEntity postEntity = createPublishedPostEntityBuilderWithUuid().build();
        SiteMemberEntity memberEntity = createMemberBasicUserEntity().builder().uuid(post.getAuthorId().getValue()).build();
        CommPrimaryCategoryEntity primaryCategoryEntity = createTestCommPrimaryCategoryEntity().builder().uuid(post.getPrimaryCategoryId().getValue()).build();
        CommSecondaryCategoryEntity secondaryCategoryEntity = createTestCommSecondaryCategoryEntity().builder().uuid(post.getSecondaryCategoryId().getValue()).build();
        long viewCount = 0L;
        PostDetailReadModel expectedReadModel = new PostDetailReadModel(
                post.getPostId().getValue(),
                post.getPrimaryCategoryId().getValue(),
                primaryCategoryEntity.getCategory(),
                post.getSecondaryCategoryId().getValue(),
                secondaryCategoryEntity.getCategory(),
                post.getAuthorId().getValue(),
                memberEntity.getNickname(),
                post.getPostContent().getTitle(),
                post.getPostContent().getContent(),
                post.getLikeCount().getValue(),
                post.getStatus().isPublished(),
                LocalDateTime.now()
        );

        given(authorJpaRepository.findByUuid(post.getAuthorId().getValue())).willReturn(Optional.of(memberEntity));
        given(authorJpaRepository.findByUuid(post.getCreateAuthorId().getValue())).willReturn(Optional.of(memberEntity));
        given(primaryCategoryJpaRepository.findByUuid(post.getPrimaryCategoryId().getValue())).willReturn(Optional.of(primaryCategoryEntity));
        given(secondaryCategoryJpaRepository.findByUuid(post.getSecondaryCategoryId().getValue())).willReturn(Optional.of(secondaryCategoryEntity));
        given(postViewCountRedisRepository.read(post.getPostId())).willReturn(viewCount);
        given(postJpaMapper.toPostEntity(post, memberEntity, memberEntity, primaryCategoryEntity, secondaryCategoryEntity, viewCount)).willReturn(postEntity);
        given(postJpaRepository.save(postEntity)).willReturn(postEntity);
        given(postJpaMapper.toPostDetailReadModel(postEntity)).willReturn(expectedReadModel);


        // when
        PostDetailReadModel result = postRepositoryJpaAdapter.save(post);

        // then
        assertThat(result).isEqualTo(expectedReadModel);
        verify(authorJpaRepository, times(2)).findByUuid(any(UUID.class));
        verify(primaryCategoryJpaRepository).findByUuid(post.getPrimaryCategoryId().getValue());
        verify(secondaryCategoryJpaRepository).findByUuid(post.getSecondaryCategoryId().getValue());
        verify(postViewCountRedisRepository).read(any(PostId.class));
        verify(postJpaMapper).toPostEntity(post, memberEntity, memberEntity, primaryCategoryEntity, secondaryCategoryEntity, viewCount);
        verify(postJpaRepository).save(postEntity);
        verify(postJpaMapper).toPostDetailReadModel(postEntity);
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
    @DisplayName("필터 조건과 페이지 정보로 PostSummaryReadModel 가져오기")
    void testGetPublishedPosts_givenFiltersAndPageable_willReturnPostSummaryReadModelList() {
        // given
        PrimaryCategoryId primaryCategoryId = testPrimaryCategoryId;
        List<SecondaryCategoryId> secondaryCategoryIds = List.of(testSecondaryCategoryId,testSecondaryCategoryId2);
        String keyword = "식물";
        Pageable pageable = PageRequest.of(0, 10);
        Page<PostSummaryReadModel> expectedPage = new PageImpl<>(List.of(TEST_POST_SUMMARY_READ_MODEL), PageRequest.of(0, 10), 1);

        given(postJpaRepository.findByDynamicConditionsAndIsPublishedTrue(
                testPrimaryCategoryId.getValue(),
                List.of(testSecondaryCategoryId.getValue(),testSecondaryCategoryId2.getValue()),
                keyword,
                pageable
        )).willReturn(expectedPage);


        // when
        Page<PostSummaryReadModel> result = postRepositoryJpaAdapter.getPublishedPosts(primaryCategoryId, secondaryCategoryIds,keyword, pageable);

        // then
        assertThat(result).isEqualTo(expectedPage);
        verify(postJpaRepository).findByDynamicConditionsAndIsPublishedTrue(
                testPrimaryCategoryId.getValue(),
                List.of(testSecondaryCategoryId.getValue(),testSecondaryCategoryId2.getValue()),
                keyword,
                pageable
        );
    }

    @Test
    @DisplayName("사용자id, 필터조건, 페이지 정보로 PostSummaryReadModel 가져오기")
    void testGetPublishedPostsByAuthor_givenAuthorIdAndFilterAndPageable_willReturnPostSummaryReadModelList() {
        // given
        AuthorId authorId = testAuthorId;
        PrimaryCategoryId primaryCategoryId = testPrimaryCategoryId;
        List<SecondaryCategoryId> secondaryCategoryIds = List.of(testSecondaryCategoryId,testSecondaryCategoryId2);
        String keyword = "식물";
        Pageable pageable = PageRequest.of(0, 10);
        Page<PostSummaryReadModel> expectedPage = new PageImpl<>(List.of(TEST_POST_SUMMARY_READ_MODEL), PageRequest.of(0, 10), 1);

        given(postJpaRepository.findByAuthMemberAndDynamicConditionsAndIsPublishedTrue(
                testAuthorId.getValue(),
                testPrimaryCategoryId.getValue(),
                List.of(testSecondaryCategoryId.getValue(),testSecondaryCategoryId2.getValue()),
                keyword,
                pageable
        )).willReturn(expectedPage);

        // when
        Page<PostSummaryReadModel> result = postRepositoryJpaAdapter.getPublishedPostsByAuthor(authorId,primaryCategoryId,secondaryCategoryIds,keyword,pageable);

        // then
        assertThat(result).isEqualTo(expectedPage);
        verify(postJpaRepository).findByAuthMemberAndDynamicConditionsAndIsPublishedTrue(
                testAuthorId.getValue(),
                testPrimaryCategoryId.getValue(),
                List.of(testSecondaryCategoryId.getValue(),testSecondaryCategoryId2.getValue()),
                keyword,
                pageable
        );
    }

    @Test
    @DisplayName("사용자id, 페이지 정보로 임시저장된 PostSummaryReadModel 가져오기")
    void testGetDraftPostsByAuthor_givenAuthorIdAndPageable_willReturnPostSummaryReadModelList() {
        // given
        AuthorId authorId = testAuthorId;
        SiteMemberEntity memberEntity = createMemberBasicUserEntity().builder().uuid(testAuthorId.getValue()).build();
        Pageable pageable = PageRequest.of(0, 10);
        PostEntity postEntity = createDraftPostEntityBuilderWithUuid().build();
        Page<PostEntity> postEntityPage = new PageImpl<>(List.of(postEntity),pageable,1);
        Page<PostSummaryReadModel> expectedPage = new PageImpl<>(List.of(TEST_POST_SUMMARY_READ_MODEL), PageRequest.of(0, 10), 1);

        given(authorJpaRepository.findByUuid(authorId.getValue())).willReturn(Optional.of(memberEntity));
        given(postJpaRepository.findByAuthMemberAndIsPublishedFalseOrderByUpdatedAtDesc(memberEntity, pageable))
                .willReturn(postEntityPage);
        given(postJpaMapper.toPostSummaryReadModel(postEntity)).willReturn(TEST_POST_SUMMARY_READ_MODEL);

        // when
        Page<PostSummaryReadModel> result = postRepositoryJpaAdapter.getDraftPostsByAuthor(authorId, pageable);

        // then
        assertThat(result).isEqualTo(expectedPage);
        verify(authorJpaRepository).findByUuid(authorId.getValue());
        verify(postJpaRepository).findByAuthMemberAndIsPublishedFalseOrderByUpdatedAtDesc(memberEntity, pageable);
        verify(postJpaMapper).toPostSummaryReadModel(postEntity);
    }

    @Test
    @DisplayName("postid로 Post 가져오기")
    void testGetPostByUlid_givenPostId_willReturnPost() {
        // given
        PostEntity postEntity = createDraftPostEntityBuilder().ulid(testPostId.getValue()).build();
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
    @DisplayName("postid로 PostDetailReadModel 가져오기")
    void testGetPostDetailByUlid_givenPostId_willReturnPostDetailReadModel() {
        // given
        PostEntity postEntity = createDraftPostEntityBuilder().ulid(testPostId.getValue()).build();
        PostDetailReadModel expectedPostDetailReadModel = TEST_PUBLISHED_POST_DETAIL_READ_MODEL;

        given(postJpaRepository.findByUlid(testPostId.getValue())).willReturn(Optional.of(postEntity));
        given(postJpaMapper.toPostDetailReadModel(postEntity)).willReturn(expectedPostDetailReadModel);

        // when
        Optional<PostDetailReadModel> result = postRepositoryJpaAdapter.getPostDetailByUlid(testPostId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedPostDetailReadModel);
        verify(postJpaRepository).findByUlid(testPostId.getValue());
        verify(postJpaMapper).toPostDetailReadModel(postEntity);
    }

    @Test
    @DisplayName("postid로 조회수 가져오기")
    void testGetViewCountByUlid_givenPostId_willReturnViewCount() {
        // given
        PostEntity postEntity = createDraftPostEntityBuilder().ulid(testPostId.getValue()).build();
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