package kr.modusplant.domains.post.framework.out.jpa.mapper;

import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PostJpaMapper;
import kr.modusplant.domains.post.usecase.model.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.model.PostSummaryReadModel;
import kr.modusplant.framework.out.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.domains.post.common.util.framework.out.jpa.entity.PostEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.CommPrimaryCategoryEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PostJpaMapperImplTest implements PostEntityTestUtils, SiteMemberEntityTestUtils, CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils {
    private final PostJpaMapper postJpaMapper = new PostJpaMapperImpl();

    @Test
    @DisplayName("toPostEntity로 엔티티 반환하기")
    void testToPostEntity_givenPostAndMemberEntityAndCategoryEntityAndViewCount_willReturnPostEntity() {
        // given
        Post post = createPublishedPost();
        SiteMemberEntity memberEntity = createMemberBasicUserEntity().builder().uuid(testAuthorId.getValue()).build();
        CommPrimaryCategoryEntity primaryCategoryEntity = createTestCommPrimaryCategoryEntity().builder().uuid(testPrimaryCategoryId.getValue()).build();
        CommSecondaryCategoryEntity secondaryCategoryEntity = createTestCommSecondaryCategoryEntity().builder().uuid(testSecondaryCategoryId.getValue()).build();
        long viewCount = 5L;

        // when
        PostEntity result = postJpaMapper.toPostEntity(
                post,
                memberEntity,
                memberEntity,
                primaryCategoryEntity,
                secondaryCategoryEntity,
                viewCount
        );

        // then
        assertThat(result.getUlid()).isEqualTo(post.getPostId().getValue());
        assertThat(result.getAuthMember()).isEqualTo(memberEntity);
        assertThat(result.getCreateMember()).isEqualTo(memberEntity);
        assertThat(result.getPrimaryCategory()).isEqualTo(primaryCategoryEntity);
        assertThat(result.getSecondaryCategory()).isEqualTo(secondaryCategoryEntity);
        assertThat(result.getTitle()).isEqualTo(post.getPostContent().getTitle());
        assertThat(result.getContent()).isEqualTo(post.getPostContent().getContent());
        assertThat(result.getLikeCount()).isEqualTo(post.getLikeCount().getValue());
        assertThat(result.getViewCount()).isEqualTo(viewCount);
        assertThat(result.getIsPublished()).isTrue();
    }

    @Test
    @DisplayName("toPost로 aggregate 반환하기")
    void testToPost_givenPostEntity_willReturnPost() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntity().builder().uuid(testAuthorId.getValue()).build();
        CommPrimaryCategoryEntity primaryCategoryEntity = createTestCommPrimaryCategoryEntity().builder().uuid(testPrimaryCategoryId.getValue()).build();
        CommSecondaryCategoryEntity secondaryCategoryEntity = createTestCommSecondaryCategoryEntity().builder().uuid(testSecondaryCategoryId.getValue()).build();
        PostEntity postEntity = createPublishedPostEntityBuilderWithUuid()
                .primaryCategory(primaryCategoryEntity)
                .secondaryCategory(secondaryCategoryEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .publishedAt(LocalDateTime.now())
                .build();

        // when
        Post result = postJpaMapper.toPost(postEntity);

        // then
        assertThat(result.getPostId().getValue()).isEqualTo(postEntity.getUlid());
        assertThat(result.getAuthorId().getValue()).isEqualTo(testAuthorId.getValue());
        assertThat(result.getPrimaryCategoryId().getValue()).isEqualTo(testPrimaryCategoryId.getValue());
        assertThat(result.getSecondaryCategoryId().getValue()).isEqualTo(testSecondaryCategoryId.getValue());
        assertThat(result.getPostContent().getTitle()).isEqualTo(postEntity.getTitle());
        assertThat(result.getPostContent().getContent()).isEqualTo(postEntity.getContent());
        assertThat(result.getLikeCount().getValue()).isEqualTo(postEntity.getLikeCount());
        assertThat(result.getStatus().isPublished()).isTrue();
    }

    @Test
    @DisplayName("toPostSummaryReadModel로 PostSummaryReadModel 반환하기")
    void testToPostSummaryReadModel_givenPostEntity_willReturnPostSummaryReadModel() {
        // given
        LocalDateTime publishedAt = LocalDateTime.now();
        SiteMemberEntity memberEntity = createMemberBasicUserEntity().builder().uuid(testAuthorId.getValue()).build();
        CommPrimaryCategoryEntity primaryCategoryEntity = createTestCommPrimaryCategoryEntity().builder().uuid(testPrimaryCategoryId.getValue()).build();
        CommSecondaryCategoryEntity secondaryCategoryEntity = createTestCommSecondaryCategoryEntity().builder().uuid(testSecondaryCategoryId.getValue()).build();
        PostEntity postEntity = createPublishedPostEntityBuilderWithUuid()
                .primaryCategory(primaryCategoryEntity)
                .secondaryCategory(secondaryCategoryEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .publishedAt(publishedAt)
                .build();

        // when
        PostSummaryReadModel result = postJpaMapper.toPostSummaryReadModel(postEntity);

        // then
        assertThat(result.ulid()).isEqualTo(postEntity.getUlid());
        assertThat(result.primaryCategory()).isEqualTo(primaryCategoryEntity.getCategory());
        assertThat(result.secondaryCategory()).isEqualTo(secondaryCategoryEntity.getCategory());
        assertThat(result.nickname()).isEqualTo(memberEntity.getNickname());
        assertThat(result.title()).isEqualTo(postEntity.getTitle());
        assertThat(result.content()).isEqualTo(postEntity.getContent());
        assertThat(result.publishedAt()).isEqualTo(publishedAt);

    }

    @Test
    @DisplayName("toPostDetailReadModel로 PostDetailReadModel 반환하기")
    void testToPostDetailReadModel_givenPostEntity_willReturnPostDetailReadModel() {
        // given
        LocalDateTime publishedAt = LocalDateTime.now();
        SiteMemberEntity memberEntity = createMemberBasicUserEntity().builder().uuid(testAuthorId.getValue()).build();
        CommPrimaryCategoryEntity primaryCategoryEntity = createTestCommPrimaryCategoryEntity().builder().uuid(testPrimaryCategoryId.getValue()).build();
        CommSecondaryCategoryEntity secondaryCategoryEntity = createTestCommSecondaryCategoryEntity().builder().uuid(testSecondaryCategoryId.getValue()).build();
        PostEntity postEntity = createPublishedPostEntityBuilderWithUuid()
                .primaryCategory(primaryCategoryEntity)
                .secondaryCategory(secondaryCategoryEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .publishedAt(publishedAt)
                .build();

                // when
        PostDetailReadModel result = postJpaMapper.toPostDetailReadModel(postEntity);

        // then
        assertThat(result.ulid()).isEqualTo(postEntity.getUlid());
        assertThat(result.primaryCategoryUuid()).isEqualTo(testPrimaryCategoryId.getValue());
        assertThat(result.primaryCategory()).isEqualTo(primaryCategoryEntity.getCategory());
        assertThat(result.secondaryCategoryUuid()).isEqualTo(testSecondaryCategoryId.getValue());
        assertThat(result.secondaryCategory()).isEqualTo(secondaryCategoryEntity.getCategory());
        assertThat(result.authorUuid()).isEqualTo(testAuthorId.getValue());
        assertThat(result.nickname()).isEqualTo(memberEntity.getNickname());
        assertThat(result.title()).isEqualTo(postEntity.getTitle());
        assertThat(result.content()).isEqualTo(postEntity.getContent());
        assertThat(result.likeCount()).isEqualTo(postEntity.getLikeCount());
        assertThat(result.isPublished()).isEqualTo(postEntity.getIsPublished());
        assertThat(result.publishedAt()).isEqualTo(publishedAt);
    }

}