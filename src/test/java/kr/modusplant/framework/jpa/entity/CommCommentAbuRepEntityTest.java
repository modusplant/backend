package kr.modusplant.framework.jpa.entity;

import kr.modusplant.framework.jpa.entity.common.util.CommCommentAbuRepEntityTestUtils;
import kr.modusplant.framework.jpa.repository.CommPrimaryCategoryJpaRepository;
import kr.modusplant.framework.jpa.repository.CommSecondaryCategoryJpaRepository;
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
class CommCommentAbuRepEntityTest implements CommCommentAbuRepEntityTestUtils {
    private final TestEntityManager entityManager;
    private final CommPrimaryCategoryJpaRepository primaryCategoryJpaRepository;
    private final CommSecondaryCategoryJpaRepository secondaryCategoryJpaRepository;

    private CommCommentAbuRepEntity commentAbuRepEntity;

    @Autowired
    CommCommentAbuRepEntityTest(TestEntityManager entityManager, CommPrimaryCategoryJpaRepository primaryCategoryJpaRepository, CommSecondaryCategoryJpaRepository secondaryCategoryJpaRepository) {
        this.entityManager = entityManager;
        this.primaryCategoryJpaRepository = primaryCategoryJpaRepository;
        this.secondaryCategoryJpaRepository = secondaryCategoryJpaRepository;
    }

    @BeforeEach
    public void beforeEach() {
        CommPrimaryCategoryEntity primaryCategoryEntity = primaryCategoryJpaRepository.findById(1).orElseThrow();
        CommSecondaryCategoryEntity secondaryCategoryEntity = secondaryCategoryJpaRepository.findById(1).orElseThrow();
        SiteMemberEntity memberEntity = entityManager.persist(createMemberBasicUserEntity());
        CommPostEntity postEntity = entityManager.persist(
                createCommPostEntityBuilder()
                        .primaryCategory(primaryCategoryEntity)
                        .secondaryCategory(secondaryCategoryEntity)
                        .authMember(memberEntity)
                        .build());
        CommCommentEntity commentEntity = entityManager.persist(
                createCommCommentEntityBuilder()
                        .post(postEntity)
                        .authMember(memberEntity).build());
        commentAbuRepEntity =
                createCommCommentAbuRepEntityBuilder()
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
                        commentAbuRepEntity.getVersionNumber());
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
        assertEquals(commentAbuRepEntity, CommCommentAbuRepEntity.builder().commCommentAbuRep(commentAbuRepEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(commentAbuRepEntity.hashCode(), commentAbuRepEntity.hashCode());
    }
}