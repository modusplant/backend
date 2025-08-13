package kr.modusplant.legacy.domains.term.persistence.repository;

import kr.modusplant.global.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.term.common.util.entity.TermEntityTestUtils;
import kr.modusplant.legacy.domains.term.persistence.entity.TermEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class TermRepositoryTest implements TermEntityTestUtils {

    private final TermRepository termRepository;

    @Autowired
    TermRepositoryTest(TermRepository termRepository) {
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
}