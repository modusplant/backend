package kr.modusplant.global.persistence.entity;

import kr.modusplant.support.context.RepositoryOnlyContext;
import kr.modusplant.support.util.entity.TermEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static kr.modusplant.global.util.VersionUtils.createVersion;
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

    @DisplayName("약관 PreUpdate")
    @Test
    void preUpdate() {
        // given
        TermEntity term = TermEntity.builder().termEntity(createTermsOfUseEntity()).build();
        entityManager.persist(term);

        // when
        entityManager.merge(TermEntity.builder().termEntity(term).version(null).build());
        entityManager.flush();

        // then
        assertThat(term.getVersion()).isEqualTo(createVersion(1, 0, 0));
    }
}