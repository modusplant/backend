package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.TermEntity;
import kr.modusplant.framework.jpa.entity.common.util.TermEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RepositoryOnlyContext
class TermJpaRepositoryTest implements TermEntityTestUtils {

    private final TermJpaRepository termRepository;

    @Autowired
    TermJpaRepositoryTest(TermJpaRepository termRepository) {
        this.termRepository = termRepository;
    }

    @DisplayName("uuid로 약관 찾기")
    @Test
    void findByUuidTest() {
        // given
        TermEntity term = createTermsOfUseEntity();

        // when
        termRepository.save(term);

        // then
        assertThat(termRepository.findByUuid(term.getUuid()).orElseThrow()).isEqualTo(term);
    }

    @DisplayName("name으로 약관 찾기")
    @Test
    void findByNameTest() {
        // given
        TermEntity term = createTermsOfUseEntity();

        // when
        termRepository.save(term);

        // then
        assertThat(termRepository.findByName(term.getName()).orElseThrow()).isEqualTo(term);
    }

    @DisplayName("version으로 약관 찾기")
    @Test
    void findByVersionTest() {
        // given
        TermEntity term = createTermsOfUseEntity();

        // when
        termRepository.save(term);

        // then
        assertThat(termRepository.findByVersion(term.getVersion()).getFirst()).isEqualTo(term);
    }

    @DisplayName("createdAt으로 약관 찾기")
    @Test
    void findByCreatedAtTest() {
        // given
        TermEntity term = createTermsOfUseEntity();

        // when
        termRepository.save(term);

        // then
        assertThat(termRepository.findByCreatedAt(term.getCreatedAt()).getFirst()).isEqualTo(term);
    }

    @DisplayName("lastModifiedAt으로 약관 찾기")
    @Test
    void findByLastModifiedAtTest() {
        // given
        TermEntity term = createTermsOfUseEntity();

        // when
        termRepository.save(term);

        // then
        assertThat(termRepository.findByLastModifiedAt(term.getLastModifiedAt()).getFirst()).isEqualTo(term);
    }

    @DisplayName("uuid로 약관 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        TermEntity term = termRepository.save(createTermsOfUseEntity());
        UUID uuid = term.getUuid();

        // when
        termRepository.deleteByUuid(uuid);

        // then
        assertThat(termRepository.findByUuid(uuid)).isEmpty();
    }

    @DisplayName("uuid로 약관 확인")
    @Test
    void existsByUuidTest() {
        // given
        TermEntity term = createTermsOfUseEntity();

        // when
        termRepository.save(term);

        // then
        assertThat(termRepository.existsByUuid(term.getUuid())).isEqualTo(true);
    }

    @DisplayName("약관 엔터티 toString 호출 시 순환 오류 발생 여부 확인")
    @Test
    void testToString_givenTermEntity_willReturnRepresentative() {
        // given
        TermEntity term = createTermsOfUseEntity();

        // when
        TermEntity termEntity = termRepository.save(term);

        // then
        assertDoesNotThrow(termEntity::toString);
    }
}