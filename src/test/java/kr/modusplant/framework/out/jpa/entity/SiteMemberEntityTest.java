package kr.modusplant.framework.out.jpa.entity;

import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberEntityTest implements SiteMemberEntityConstant {

    private final TestEntityManager entityManager;

    @Autowired
    SiteMemberEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @DisplayName("회원 PrePersist")
    @Test
    void prePersist() {
        // given
        SiteMemberEntity member = SiteMemberEntity.builder().memberEntity(createMemberBasicUserEntity()).isActive(null).isDisabledByLinking(null).isBanned(null).isDeleted(null).build();

        // when
        entityManager.persist(member);
        entityManager.flush();

        // then
        assertThat(member.getIsActive()).isEqualTo(true);
        assertThat(member.getIsDisabledByLinking()).isEqualTo(false);
        assertThat(member.getIsBanned()).isEqualTo(false);
        assertThat(member.getIsDeleted()).isEqualTo(false);
    }

    @DisplayName("회원 PreUpdate")
    @Test
    void preUpdate() {
        // given
        SiteMemberEntity member = SiteMemberEntity.builder().memberEntity(createMemberBasicUserEntity()).build();
        entityManager.persist(member);

        // when
        entityManager.merge(SiteMemberEntity.builder().memberEntity(member).isActive(null).isDisabledByLinking(null).isBanned(null).isDeleted(null).build());
        entityManager.flush();

        // then
        assertThat(member.getIsActive()).isEqualTo(true);
        assertThat(member.getIsDisabledByLinking()).isEqualTo(false);
        assertThat(member.getIsBanned()).isEqualTo(false);
        assertThat(member.getIsDeleted()).isEqualTo(false);
    }
}