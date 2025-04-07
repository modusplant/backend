<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/member/persistence/entity/SiteMemberRoleEntityTest.java
package kr.modusplant.domains.member.persistence.entity;

import kr.modusplant.domains.member.common.util.entity.SiteMemberRoleEntityTestUtils;
========
package kr.modusplant.api.crud.member.persistence.entity;

import kr.modusplant.api.crud.member.common.util.entity.SiteMemberRoleEntityTestUtils;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/test/java/kr/modusplant/api/crud/member/persistence/entity/SiteMemberRoleEntityTest.java
import kr.modusplant.global.context.RepositoryOnlyContext;
import kr.modusplant.global.enums.Role;
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
        assertThat(memberRoleEntity.getRole()).isEqualTo(Role.ROLE_USER);
    }
}