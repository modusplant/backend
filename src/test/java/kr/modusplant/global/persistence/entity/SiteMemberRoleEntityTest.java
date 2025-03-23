package kr.modusplant.global.persistence.entity;

import kr.modusplant.global.enums.Role;
import kr.modusplant.support.context.RepositoryOnlyContext;
import kr.modusplant.support.util.entity.SiteMemberRoleEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberRoleEntityTest implements SiteMemberRoleEntityTestUtils {

    private final TestEntityManager entityManager;

    @Autowired
    SiteMemberRoleEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @DisplayName("회원 역할 PrePersist")
    @Test
    void prePersist() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        SiteMemberRoleEntity memberRole = SiteMemberRoleEntity.builder().member(member).role(Role.ROLE_ADMIN).build();

        // when
        entityManager.persist(memberRole);
        entityManager.flush();

        // then
        assertThat(memberRole.getRole()).isEqualTo(Role.ROLE_ADMIN);
    }
}