<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/term/mapper/TermEntityMapperTest.java
package kr.modusplant.domains.term.mapper;

import kr.modusplant.domains.term.common.util.domain.TermTestUtils;
import kr.modusplant.domains.term.common.util.entity.TermEntityTestUtils;
import kr.modusplant.domains.term.persistence.entity.TermEntity;
import kr.modusplant.domains.term.persistence.repository.TermCrudJpaRepository;
========
package kr.modusplant.api.crud.term.mapper;

import kr.modusplant.api.crud.term.common.util.domain.TermTestUtils;
import kr.modusplant.api.crud.term.common.util.entity.TermEntityTestUtils;
import kr.modusplant.api.crud.term.persistence.entity.TermEntity;
import kr.modusplant.api.crud.term.persistence.repository.TermJpaRepository;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/test/java/kr/modusplant/api/crud/term/mapper/TermEntityMapperTest.java
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class TermEntityMapperTest implements TermTestUtils, TermEntityTestUtils {

    private final TermCrudJpaRepository termRepository;
    private final TermEntityMapper termMapper = new TermEntityMapperImpl();

    @Autowired
    TermEntityMapperTest(TermCrudJpaRepository termRepository) {
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