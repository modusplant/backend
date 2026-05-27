package kr.modusplant.domains.member.framework.outbound.jpa.entity;

import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.CommentAbuseReportEntityTestUtils;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.SecondaryCategoryEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.PrimaryCategoryJpaRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.SecondaryCategoryJpaRepository;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RepositoryOnlyContext
class CommentAbuseReportEntityTest implements CommentAbuseReportEntityTestUtils {
    private final TestEntityManager entityManager;
    private final PrimaryCategoryJpaRepository primaryCategoryJpaRepository;
    private final SecondaryCategoryJpaRepository secondaryCategoryJpaRepository;

    private CommentAbuseReportEntity commentAbuRepEntity;

    @Autowired
    CommentAbuseReportEntityTest(TestEntityManager entityManager, PrimaryCategoryJpaRepository primaryCategoryJpaRepository, SecondaryCategoryJpaRepository secondaryCategoryJpaRepository) {
        this.entityManager = entityManager;
        this.primaryCategoryJpaRepository = primaryCategoryJpaRepository;
        this.secondaryCategoryJpaRepository = secondaryCategoryJpaRepository;
    }

    @BeforeEach
    public void beforeEach() {
        PrimaryCategoryEntity primaryCategoryEntity = primaryCategoryJpaRepository.findById(1).orElseThrow();
        SecondaryCategoryEntity secondaryCategoryEntity = secondaryCategoryJpaRepository.findById(1).orElseThrow();
        MemberEntity memberEntity = entityManager.persist(createMemberBasicUserEntity());
        PostEntity postEntity = entityManager.persist(
                createPostEntityBuilder()
                        .primaryCategory(primaryCategoryEntity)
                        .secondaryCategory(secondaryCategoryEntity)
                        .authMember(memberEntity)
                        .build());
        CommentEntity commentEntity = entityManager.persist(
                createCommentEntityBuilder()
                        .post(postEntity)
                        .authMember(memberEntity).build());
        commentAbuRepEntity =
                createCommentAbuseReportEntityBuilder()
                        .member(memberEntity)
                        .comment(commentEntity).build();
    }

    @Test
    @DisplayName("getETagSource를 통해 ETag 소스 반환")
    void testGetETagSource_givenNothing_willReturnETagSource() {
        assertEquals(commentAbuRepEntity.getETagSource(),
                commentAbuRepEntity.getMemberId() + "-" +
                        commentAbuRepEntity.getComment().getPost().getUlid() + "-" +
                        commentAbuRepEntity.getComment().getPath() + "-" +
                        commentAbuRepEntity.getCheckedAt());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(commentAbuRepEntity, commentAbuRepEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(commentAbuRepEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(commentAbuRepEntity, CommentAbuseReportEntity.builder().commentAbuseReport(commentAbuRepEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(commentAbuRepEntity.hashCode(), commentAbuRepEntity.hashCode());
    }
}