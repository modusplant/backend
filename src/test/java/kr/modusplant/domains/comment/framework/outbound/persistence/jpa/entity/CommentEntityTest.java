package kr.modusplant.domains.comment.framework.outbound.persistence.jpa.entity;

import kr.modusplant.domains.comment.common.util.framework.outbound.persistence.jpa.entity.CommentEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.SecondaryCategoryEntity;
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
public class CommentEntityTest implements CommentEntityTestUtils {

    private final TestEntityManager entityManager;
    private CommentEntity commentEntity;

    @Autowired
    CommentEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @BeforeEach
    public void beforeEach() {
        PrimaryCategoryEntity primaryCategoryEntity = createPrimaryCategoryEntityWithId();
        MemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        commentEntity = createCommentEntityBuilder()
                .post(
                        createPublishedPostEntityBuilder()
                                .primaryCategory(primaryCategoryEntity)
                                .secondaryCategory(
                                        createSecondaryCategoryEntityBuilderWithId()
                                                .primaryCategory(primaryCategoryEntity)
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
        MemberEntity member = createMemberBasicUserEntity();
        PrimaryCategoryEntity primaryCategory = createPrimaryCategoryEntity();
        SecondaryCategoryEntity secondaryCategory = createSecondaryCategoryEntityBuilder().primaryCategory(primaryCategory).build();
        entityManager.persist(primaryCategory);
        entityManager.persist(secondaryCategory);
        PostEntity postEntity = createPublishedPostEntityBuilder()
                .primaryCategory(primaryCategory)
                .secondaryCategory(secondaryCategory)
                .authMember(member)
                .likeCount(1)
                .viewCount(1L)
                .isPublished(true)
                .build();

        CommentEntity commentEntity = createCommentEntityBuilder()
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
        commentEntity.markAsDeleted();

        // then
        assertEquals(commentEntity.getIsDeleted(), true);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(commentEntity, commentEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(commentEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(commentEntity, CommentEntity.builder().comment(commentEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(commentEntity.hashCode(), commentEntity.hashCode());
    }
}
