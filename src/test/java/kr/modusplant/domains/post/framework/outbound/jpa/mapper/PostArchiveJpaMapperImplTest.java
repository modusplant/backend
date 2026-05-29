package kr.modusplant.domains.post.framework.outbound.jpa.mapper;

import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity.PostArchiveEntityTestUtils;
import kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity.PostEntityTestUtils;
import kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity.PrimaryCategoryEntityTestUtils;
import kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity.SecondaryCategoryEntityTestUtils;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostArchiveEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.SecondaryCategoryEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.supers.PostArchiveJpaMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_PUBLISHED_AT;
import static org.assertj.core.api.Assertions.assertThat;

class PostArchiveJpaMapperImplTest implements PostEntityTestUtils, PostArchiveEntityTestUtils, MemberEntityTestUtils, PrimaryCategoryEntityTestUtils, SecondaryCategoryEntityTestUtils {
    private final PostArchiveJpaMapper postArchiveJpaMapper = new PostArchiveJpaMapperImpl();

    @Test
    @DisplayName("toPostArchiveEntity로 엔티티 반환하기")
    void testToPostArchiveEntity_givenPostEntity_willReturnPostArchiveEntity() {
        // given
        MemberEntity memberEntity = MemberEntity.builder().uuid(testAuthorId.getValue()).build();
        PrimaryCategoryEntity primaryCategoryEntity = PrimaryCategoryEntity.builder().id(testPrimaryCategoryId.getValue()).build();
        SecondaryCategoryEntity secondaryCategoryEntity = createSecondaryCategoryEntityBuilder().id(testSecondaryCategoryId.getValue()).build();
        PostEntity postEntity = createPublishedPostEntityBuilderWithUuid()
                .primaryCategory(primaryCategoryEntity)
                .secondaryCategory(secondaryCategoryEntity)
                .authMember(memberEntity)
                .publishedAt(TEST_POST_PUBLISHED_AT)
                .build();

        // when
        PostArchiveEntity result = postArchiveJpaMapper.toPostArchiveEntity(postEntity);

        // then
        assertThat(result.getUlid()).isEqualTo(postEntity.getUlid());
        assertThat(result.getPrimaryCategoryId()).isEqualTo(primaryCategoryEntity.getId());
        assertThat(result.getSecondaryCategoryId()).isEqualTo(secondaryCategoryEntity.getId());
        assertThat(result.getAuthMemberUuid()).isEqualTo(memberEntity.getUuid());
        assertThat(result.getTitle()).isEqualTo(postEntity.getTitle());
        assertThat(result.getContentText()).isEqualTo(postEntity.getContentText());
        assertThat(result.getCreatedAt()).isEqualTo(postEntity.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(postEntity.getUpdatedAt());
        assertThat(result.getPublishedAt()).isEqualTo(postEntity.getPublishedAt());
    }

    @Test
    @DisplayName("toPostArchiveEntity로 탈퇴한 회원을 가진 엔티티 반환하기")
    void testToPostArchiveEntity_givenPostEntityWithNullMember_willReturnPostArchiveEntity() {
        // given
        MemberEntity memberEntity = null;
        PrimaryCategoryEntity primaryCategoryEntity = PrimaryCategoryEntity.builder().id(testPrimaryCategoryId.getValue()).build();
        SecondaryCategoryEntity secondaryCategoryEntity = createSecondaryCategoryEntityBuilder().id(testSecondaryCategoryId.getValue()).build();
        PostEntity postEntity = createPublishedPostEntityBuilderWithUuid()
                .primaryCategory(primaryCategoryEntity)
                .secondaryCategory(secondaryCategoryEntity)
                .authMember(memberEntity)
                .publishedAt(LocalDateTime.now())
                .build();

        // when
        PostArchiveEntity result = postArchiveJpaMapper.toPostArchiveEntity(postEntity);

        // then

        assertThat(result.getAuthMemberUuid()).isNull();
    }



}