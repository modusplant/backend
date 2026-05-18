package kr.modusplant.domains.member.framework.out.jpa.entity;

import kr.modusplant.domains.member.framework.out.jpa.entity.common.util.PostAbuseReportEntityTestUtils;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.SecondaryCategoryEntity;
import kr.modusplant.domains.post.framework.out.jpa.mapper.PrimaryCategoryJpaRepository;
import kr.modusplant.domains.post.framework.out.jpa.mapper.SecondaryCategoryJpaRepository;
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
class PostAbuseReportEntityTest implements PostAbuseReportEntityTestUtils {
    private final TestEntityManager entityManager;
    private final PrimaryCategoryJpaRepository primaryCategoryJpaRepository;
    private final SecondaryCategoryJpaRepository secondaryCategoryJpaRepository;

    private PostAbuseReportEntity postAbuseReportEntity;

    @Autowired
    PostAbuseReportEntityTest(TestEntityManager entityManager, PrimaryCategoryJpaRepository primaryCategoryJpaRepository, SecondaryCategoryJpaRepository secondaryCategoryJpaRepository) {
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
        postAbuseReportEntity =
                entityManager.persist(
                        createPostAbuseReportEntityBuilder()
                                .member(memberEntity)
                                .post(postEntity)
                                .build());
    }

    @Test
    @DisplayName("getETagSource를 통해 ETag 소스 반환")
    void testGetETagSource_givenNothing_willReturnETagSource() {
        assertEquals(postAbuseReportEntity.getETagSource(),
                postAbuseReportEntity.getMemberId() + "-" +
                        postAbuseReportEntity.getPost().getUlid() + "-" +
                        postAbuseReportEntity.getVersionNumber());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(postAbuseReportEntity, postAbuseReportEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(postAbuseReportEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(postAbuseReportEntity, PostAbuseReportEntity.builder().postAbuseReport(postAbuseReportEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(postAbuseReportEntity.hashCode(), postAbuseReportEntity.hashCode());
    }
}