package kr.modusplant.framework.jpa.entity;

import kr.modusplant.framework.jpa.entity.common.util.CommPostEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RepositoryOnlyContext
class CommPostEntityTest implements CommPostEntityTestUtils {

    private final TestEntityManager entityManager;

    @Autowired
    CommPostEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Test
    @DisplayName("컨텐츠 게시글 PrePersist")
    void prePersist() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        CommPrimaryCategoryEntity commPrimaryCategoryEntity = entityManager.merge(createCommPrimaryCategoryEntity());
        CommSecondaryCategoryEntity commSecondaryCategoryEntity = entityManager.merge(createCommSecondaryCategoryEntityBuilder().primaryCategory(commPrimaryCategoryEntity).build());
        CommPostEntity commPost = createCommPostEntityBuilder()
                .primaryCategory(commPrimaryCategoryEntity)
                .secondaryCategory(commSecondaryCategoryEntity)
                .authMember(member)
                .likeCount(null)
                .viewCount(null)
                .isPublished(null)
                .build();

        // when
        entityManager.persist(commPost);
        entityManager.flush();

        // then
        assertThat(commPost.getLikeCount()).isEqualTo(0);
        assertThat(commPost.getViewCount()).isEqualTo(0L);
        assertThat(commPost.getIsPublished()).isEqualTo(false);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("발행 여부 전환 테스트")
    void updateIsPublishedTest(Boolean isPublished) {
        CommPostEntity commPost = createCommPostEntityBuilder().build();

        commPost.updateIsPublished(isPublished);

        assertThat(commPost.getIsPublished()).isEqualTo(isPublished);
    }

    @Test
    @DisplayName("게시글 좋아요 수 증가 테스트")
    void increaseLikeCountTest() {
        CommPostEntity commPost = createCommPostEntityBuilder()
                .likeCount(0)
                .build();

        commPost.increaseLikeCount();

        assertThat(commPost.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 좋아요 수 감소 테스트")
    void decreaseLikeCountTest() {
        CommPostEntity commPost = createCommPostEntityBuilder()
                .likeCount(1)
                .build();

        commPost.decreaseLikeCount();
        assertThat(commPost.getLikeCount()).isEqualTo(0);

        commPost.decreaseLikeCount();
        assertThat(commPost.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("발행 날짜 갱신 테스트")
    void updatePublishedAtTest() {
        LocalDateTime localDateTime = LocalDateTime.now();
        CommPostEntity commPost = createCommPostEntityBuilder().build();

        commPost.updatePublishedAt(localDateTime);

        assertThat(commPost.getPublishedAt()).isEqualTo(localDateTime);
    }

    @Test
    @DisplayName("getETagSource를 통해 ETag 소스 반환")
    void testGetETagSource_givenNothing_willReturnETagSource() {
        // when
        CommPostEntity commPostEntity = createCommPostEntityBuilderWithUlid().build();

        // then
        assertEquals(commPostEntity.getETagSource(),TEST_COMM_POST_ULID + "-" + null);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        // when
        CommPostEntity commPostEntity = createCommPostEntityBuilderWithUlid().build();

        // then
        //noinspection EqualsWithItself
        assertEquals(commPostEntity, commPostEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        // when
        CommPostEntity commPostEntity = createCommPostEntityBuilderWithUlid().build();

        // then
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(commPostEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        // when
        CommPostEntity commPostEntity = createCommPostEntityBuilderWithUlid().build();

        // then
        assertEquals(commPostEntity, CommPostEntity.builder().commPost(commPostEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        // when
        CommPostEntity commPostEntity = createCommPostEntityBuilderWithUlid().build();

        // then
        assertEquals(commPostEntity.hashCode(), commPostEntity.hashCode());
    }
}