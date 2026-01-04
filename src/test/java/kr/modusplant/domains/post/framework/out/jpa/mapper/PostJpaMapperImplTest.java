package kr.modusplant.domains.post.framework.out.jpa.mapper;

import kr.modusplant.domains.post.common.util.framework.out.jpa.entity.PostEntityTestUtils;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PostJpaMapper;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import kr.modusplant.framework.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.common.util.CommPrimaryCategoryEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberEntityTestUtils;
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
        SiteMemberEntity memberEntity = SiteMemberEntity.builder().uuid(testAuthorId.getValue()).build();
        CommPrimaryCategoryEntity primaryCategoryEntity = CommPrimaryCategoryEntity.builder().id(testPrimaryCategoryId.getValue()).build();
        CommSecondaryCategoryEntity secondaryCategoryEntity = createCommSecondaryCategoryEntityBuilder().id(testSecondaryCategoryId.getValue()).build();
        long viewCount = 5L;

        // when
        CommPostEntity result = postJpaMapper.toPostEntity(
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
        SiteMemberEntity memberEntity = SiteMemberEntity.builder().uuid(testAuthorId.getValue()).build();
        CommPrimaryCategoryEntity primaryCategoryEntity = CommPrimaryCategoryEntity.builder().id(testPrimaryCategoryId.getValue()).build();
        CommSecondaryCategoryEntity secondaryCategoryEntity = createCommSecondaryCategoryEntityBuilder().id(testSecondaryCategoryId.getValue()).build();
        CommPostEntity postEntity = createPublishedPostEntityBuilderWithUuid()
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

}