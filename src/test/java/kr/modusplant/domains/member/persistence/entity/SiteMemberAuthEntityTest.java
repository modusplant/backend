package kr.modusplant.domains.member.persistence.entity;

import kr.modusplant.domains.member.common.util.entity.SiteMemberAuthEntityTestUtils;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberAuthEntityTest implements SiteMemberAuthEntityTestUtils {

    private final TestEntityManager entityManager;

    @Autowired
    SiteMemberAuthEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @DisplayName("회원 인증 PrePersist")
    @Test
    void prePersist() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        SiteMemberAuthEntity memberAuth = createMemberAuthBasicUserEntityBuilder().originalMember(member).activeMember(member).failedAttempt(1).build();

        // when
        entityManager.persist(memberAuth);
        entityManager.flush();

        // then
        assertThat(memberAuth.getFailedAttempt()).isEqualTo(1);
    }

    @DisplayName("회원 인증 PreUpdate")
    @Test
    void preUpdate() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        SiteMemberAuthEntity memberAuth = createMemberAuthBasicUserEntityBuilder().originalMember(member).activeMember(member).build();
        entityManager.persist(memberAuth);

        // when
        memberAuth.updateFailedAttempt(null);
        entityManager.flush();

        // then
        assertThat(memberAuth.getFailedAttempt()).isEqualTo(0);
    }
}