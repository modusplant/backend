package kr.modusplant.framework.jpa.entity;

import kr.modusplant.framework.jpa.entity.common.util.CommCommentEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RepositoryOnlyContext
public class CommCommentEntityTest implements CommCommentEntityTestUtils {

    private final TestEntityManager entityManager;
    private CommCommentEntity commCommentEntity;

    @Autowired
    CommCommentEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @BeforeEach
    public void beforeEach() {
        CommPrimaryCategoryEntity commPrimaryCategoryEntity = createCommPrimaryCategoryEntityWithId();
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        commCommentEntity = createCommCommentEntityBuilder()
                .post(
                        createCommPostEntityBuilder()
                                .primaryCategory(commPrimaryCategoryEntity)
                                .secondaryCategory(
                                        createCommSecondaryCategoryEntityBuilderWithId()
                                                .primaryCategory(commPrimaryCategoryEntity)
                                                .build()
                                )
                                .authMember(memberEntity)
                                .build()
                )
                .authMember(memberEntity).build();
    }

    @DisplayName("컨텐츠 댓글 PrePersist")
    @Test
    void prePersist() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        CommPrimaryCategoryEntity primaryCategory = createCommPrimaryCategoryEntity();
        CommSecondaryCategoryEntity secondaryCategory = createCommSecondaryCategoryEntityBuilder().primaryCategory(primaryCategory).build();
        entityManager.persist(primaryCategory);
        entityManager.persist(secondaryCategory);
        CommPostEntity postEntity = createCommPostEntityBuilder()
                .primaryCategory(primaryCategory)
                .secondaryCategory(secondaryCategory)
                .authMember(member)
                .likeCount(1)
                .viewCount(1L)
                .isPublished(true)
                .build();

        CommCommentEntity commentEntity = createCommCommentEntityBuilder()
                .post(postEntity)
                .authMember(member)
                .likeCount(null)
                .isDeleted(null)
                .build();

        // when
        entityManager.persist(postEntity);
        entityManager.persist(commentEntity);
        entityManager.flush();

        // then
        assertThat(commentEntity.getLikeCount()).isEqualTo(0);
        assertThat(commentEntity.getIsDeleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("markAsDeleted으로 isDeleted을 true로 전환")
    void testMarkAsDeleted_givenNothing_willConvertIsDeletedToTrue() {
        // given & when
        commCommentEntity.markAsDeleted();

        // then
        assertEquals(commCommentEntity.getIsDeleted(), true);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(commCommentEntity, commCommentEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(commCommentEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(commCommentEntity, CommCommentEntity.builder().commComment(commCommentEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(commCommentEntity.hashCode(), commCommentEntity.hashCode());
    }
}
