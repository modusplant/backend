package kr.modusplant.framework.out.jpa.entity;

import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberRoleEntityConstant;
import kr.modusplant.infrastructure.security.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberRoleEntityTest implements SiteMemberRoleEntityConstant {

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

    @DisplayName("회원 역할 PreUpdate")
    @Test
    void preUpdate() {
        // given
        SiteMemberEntity member = SiteMemberEntity.builder().memberEntity(createMemberBasicUserEntity()).build();
        SiteMemberRoleEntity memberRoleEntity = SiteMemberRoleEntity.builder().member(member).build();
        entityManager.persist(memberRoleEntity);
        entityManager.flush();
        entityManager.detach(memberRoleEntity);

        // when
        memberRoleEntity = SiteMemberRoleEntity.builder().member(member).role(null).build();
        entityManager.persist(memberRoleEntity);

        // then
        assertThat(memberRoleEntity.getRole()).isEqualTo(Role.USER);
    }
}