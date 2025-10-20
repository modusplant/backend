package kr.modusplant.framework.out.jpa.entity;

import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.term.common.util.entity.TermEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static kr.modusplant.shared.util.VersionUtils.createVersion;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class TermEntityTest implements TermEntityTestUtils {

    private final TestEntityManager entityManager;

    @Autowired
    TermEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @DisplayName("약관 PrePersist")
    @Test
    void prePersist() {
        // given
        String version = createVersion(1, 0, 1);
        TermEntity term = TermEntity.builder().termEntity(createTermsOfUseEntity()).version(version).build();

        // when
        entityManager.persist(term);
        entityManager.flush();

        // then
        assertThat(term.getVersion()).isEqualTo(version);
    }
}