package kr.modusplant.domains.term.framework.outbound.jpa.entity;

import kr.modusplant.domains.term.common.util.framework.outbound.jpa.entity.TermEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.term.common.constant.TermConstant.TEST_TERMS_OF_USE_UUID;
import static kr.modusplant.shared.util.VersionUtils.createVersion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RepositoryOnlyContext
class TermEntityTest implements TermEntityTestUtils {

    private final TestEntityManager entityManager;
    private TermEntity testTermEntity;

    @Autowired
    TermEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @BeforeEach
    public void beforeEach() {
        testTermEntity = createTermsOfUseEntityWithUuid();
    }

    @DisplayName("약관 PrePersist")
    @Test
    void prePersist() {
        // given
        String version = createVersion(1, 0, 1);
        TermEntity term = TermEntity.builder().term(createTermsOfUseEntity()).version(version).build();

        // when
        entityManager.persist(term);
        entityManager.flush();

        // then
        assertThat(term.getVersion()).isEqualTo(version);
    }

    @Test
    @DisplayName("getETagSource를 통해 ETag 소스 반환")
    void testGetETagSource_givenNothing_willReturnETagSource() {
        assertEquals(testTermEntity.getETagSource(), TEST_TERMS_OF_USE_UUID + "-" + null);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testTermEntity, testTermEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testTermEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(testTermEntity, TermEntity.builder().term(testTermEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testTermEntity.hashCode(), testTermEntity.hashCode());
    }
}