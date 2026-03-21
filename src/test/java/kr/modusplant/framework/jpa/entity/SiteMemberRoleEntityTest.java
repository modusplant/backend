package kr.modusplant.framework.jpa.entity;

import kr.modusplant.framework.jpa.entity.common.util.SiteMemberRoleEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.infrastructure.security.enums.Role;
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
class SiteMemberRoleEntityTest implements SiteMemberRoleEntityTestUtils {

    private final TestEntityManager entityManager;
    private SiteMemberRoleEntity testSiteMemberRoleEntity;

    @Autowired
    SiteMemberRoleEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @BeforeEach
    public void beforeEach() {
        testSiteMemberRoleEntity = createMemberRoleUserEntity();
    }

    @Test
    @DisplayName("회원 역할 PrePersist")
    void prePersist() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        SiteMemberRoleEntity memberRole = SiteMemberRoleEntity.builder().member(member).role(Role.ADMIN).build();

        // when
        entityManager.persist(memberRole);
        entityManager.flush();

        // then
        assertThat(memberRole.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testSiteMemberRoleEntity, testSiteMemberRoleEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testSiteMemberRoleEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(testSiteMemberRoleEntity, SiteMemberRoleEntity.builder().memberRole(testSiteMemberRoleEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testSiteMemberRoleEntity.hashCode(), testSiteMemberRoleEntity.hashCode());
    }
}