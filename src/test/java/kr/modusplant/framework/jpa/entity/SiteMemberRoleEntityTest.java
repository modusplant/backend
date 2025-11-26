package kr.modusplant.framework.jpa.entity;

import kr.modusplant.framework.jpa.entity.common.util.SiteMemberRoleEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.infrastructure.security.enums.Role;
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
        SiteMemberRoleEntity memberRole = SiteMemberRoleEntity.builder().member(member).role(Role.ADMIN).build();

        // when
        entityManager.persist(memberRole);
        entityManager.flush();

        // then
        assertThat(memberRole.getRole()).isEqualTo(Role.ADMIN);
    }
}