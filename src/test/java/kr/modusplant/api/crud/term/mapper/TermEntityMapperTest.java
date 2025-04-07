package kr.modusplant.api.crud.term.mapper;

import kr.modusplant.api.crud.term.common.util.domain.TermTestUtils;
import kr.modusplant.api.crud.term.common.util.entity.TermEntityTestUtils;
import kr.modusplant.api.crud.term.persistence.entity.TermEntity;
import kr.modusplant.api.crud.term.persistence.repository.TermJpaRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class TermEntityMapperTest implements TermTestUtils, TermEntityTestUtils {

    private final TermJpaRepository termRepository;
    private final TermEntityMapper termMapper = new TermEntityMapperImpl();

    @Autowired
    TermEntityMapperTest(TermJpaRepository termRepository) {
        this.termRepository = termRepository;
    }

    @DisplayName("매퍼 적용 후 일관된 약관 엔터티 확인")
    @Test
    void checkConsistentEntity() {
        // given
        TermEntity termEntity = createTermsOfUseEntity();

        // when
        termEntity = termRepository.save(termEntity);

        // then
        assertThat(termEntity).isEqualTo(termMapper.updateTermEntity(termMapper.toTerm(termEntity)));
    }

    @DisplayName("매퍼 적용 후 일관된 약관 도메인 확인")
    @Test
    void checkConsistentDomain() {
        assertThat(termsOfUse).isEqualTo(termMapper.toTerm(termMapper.createTermEntity(termsOfUse)));
    }
}