package kr.modusplant.domains.post.framework.out.jpa.entity;

import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.common.util.PostEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_ULID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RepositoryOnlyContext
class PostEntityTest implements PostEntityTestUtils {

    private final TestEntityManager entityManager;

    @Autowired
    PostEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Test
    @DisplayName("컨텐츠 게시글 PrePersist")
    void prePersist() {
        // given
        MemberEntity member = createMemberBasicUserEntity();
        PrimaryCategoryEntity primaryCategoryEntity = entityManager.merge(createPrimaryCategoryEntity());
        SecondaryCategoryEntity secondaryCategoryEntity = entityManager.merge(createSecondaryCategoryEntityBuilder().primaryCategory(primaryCategoryEntity).build());
        PostEntity post = createPostEntityBuilder()
                .primaryCategory(primaryCategoryEntity)
                .secondaryCategory(secondaryCategoryEntity)
                .authMember(member)
                .likeCount(null)
                .viewCount(null)
                .isPublished(null)
                .build();

        // when
        entityManager.persist(post);
        entityManager.flush();

        // then
        assertThat(post.getLikeCount()).isEqualTo(0);
        assertThat(post.getViewCount()).isEqualTo(0L);
        assertThat(post.getIsPublished()).isEqualTo(false);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("발행 여부 전환 테스트")
    void updateIsPublishedTest(Boolean isPublished) {
        PostEntity post = createPostEntityBuilder().build();

        post.updateIsPublished(isPublished);

        assertThat(post.getIsPublished()).isEqualTo(isPublished);
    }

    @Test
    @DisplayName("게시글 좋아요 수 증가 테스트")
    void increaseLikeCountTest() {
        PostEntity post = createPostEntityBuilder()
                .likeCount(0)
                .build();

        post.increaseLikeCount();

        assertThat(post.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 좋아요 수 감소 테스트")
    void decreaseLikeCountTest() {
        PostEntity post = createPostEntityBuilder()
                .likeCount(1)
                .build();

        post.decreaseLikeCount();
        assertThat(post.getLikeCount()).isEqualTo(0);

        post.decreaseLikeCount();
        assertThat(post.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("발행 날짜 갱신 테스트")
    void updatePublishedAtTest() {
        LocalDateTime localDateTime = LocalDateTime.now();
        PostEntity post = createPostEntityBuilder().build();

        post.updatePublishedAt(localDateTime);

        assertThat(post.getPublishedAt()).isEqualTo(localDateTime);
    }

    @Test
    @DisplayName("getETagSource를 통해 ETag 소스 반환")
    void testGetETagSource_givenNothing_willReturnETagSource() {
        // when
        PostEntity postEntity = createPostEntityBuilderWithUlid().build();

        // then
        assertEquals(postEntity.getETagSource(),TEST_COMM_POST_ULID + "-" + null);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        // when
        PostEntity postEntity = createPostEntityBuilderWithUlid().build();

        // then
        //noinspection EqualsWithItself
        assertEquals(postEntity, postEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        // when
        PostEntity postEntity = createPostEntityBuilderWithUlid().build();

        // then
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(postEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        // when
        PostEntity postEntity = createPostEntityBuilderWithUlid().build();

        // then
        assertEquals(postEntity, PostEntity.builder().post(postEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        // when
        PostEntity postEntity = createPostEntityBuilderWithUlid().build();

        // then
        assertEquals(postEntity.hashCode(), postEntity.hashCode());
    }
}