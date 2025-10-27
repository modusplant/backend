package kr.modusplant.domains.post.framework.out.jpa.mapper;

import kr.modusplant.domains.post.common.util.framework.out.jpa.entity.PostArchiveEntityTestUtils;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostArchiveEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PostArchiveJpaMapper;
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

class PostArchiveJpaMapperImplTest implements PostEntityTestUtils, PostArchiveEntityTestUtils, SiteMemberEntityTestUtils, CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils {
    private PostArchiveJpaMapper postArchiveJpaMapper = new PostArchiveJpaMapperImpl();

    @Test
    @DisplayName("toPostArchiveEntity로 엔티티 반환하기")
    void testToPostArchiveEntity_givenPostEntity_willReturnPostArchiveEntity() {
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
        PostArchiveEntity result = postArchiveJpaMapper.toPostArchiveEntity(postEntity);

        // then
        assertThat(result.getUlid()).isEqualTo(postEntity.getUlid());
        assertThat(result.getPrimaryCategoryUuid()).isEqualTo(primaryCategoryEntity.getUuid());
        assertThat(result.getSecondaryCategoryUuid()).isEqualTo(secondaryCategoryEntity.getUuid());
        assertThat(result.getAuthMemberUuid()).isEqualTo(memberEntity.getUuid());
        assertThat(result.getCreateMemberUuid()).isEqualTo(memberEntity.getUuid());
        assertThat(result.getTitle()).isEqualTo(postEntity.getTitle());
        assertThat(result.getContent()).isEqualTo(postEntity.getContent());
        assertThat(result.getCreatedAt()).isEqualTo(postEntity.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(postEntity.getUpdatedAt());
        assertThat(result.getPublishedAt()).isEqualTo(postEntity.getPublishedAt());
    }



}