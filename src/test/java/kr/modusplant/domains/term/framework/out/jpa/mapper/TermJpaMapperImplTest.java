package kr.modusplant.domains.term.framework.out.jpa.mapper;

import kr.modusplant.domains.term.common.util.domain.aggregate.TermTestUtils;
import kr.modusplant.domains.term.domain.aggregate.Term;
import kr.modusplant.domains.term.domain.vo.TermContent;
import kr.modusplant.domains.term.domain.vo.TermName;
import kr.modusplant.domains.term.domain.vo.TermVersion;
import kr.modusplant.domains.term.framework.out.jpa.mapper.supers.TermJpaMapper;
import kr.modusplant.framework.jpa.entity.TermEntity;
import kr.modusplant.framework.jpa.entity.common.util.TermEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.TermConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

class TermJpaMapperImplTest implements TermTestUtils, TermEntityTestUtils {
    private final TermJpaMapper termJpaMapper = new TermJpaMapperImpl();

    @Test
    @DisplayName("toTermNewEntity로 엔터티 반환")
    void testToTermNewEntity_givenValidTerm_willReturnEntity() {
        // given
        Term term = Term.create(
                TermName.create(TEST_TERMS_OF_USE_NAME),
                TermContent.create(TEST_TERMS_OF_USE_CONTENT),
                TermVersion.create(TEST_TERMS_OF_USE_VERSION)
        );

        // when
        TermEntity entity = termJpaMapper.toTermNewEntity(term);

        // then
        assertThat(entity.getName()).isEqualTo(TEST_TERMS_OF_USE_NAME);
        assertThat(entity.getContent()).isEqualTo(TEST_TERMS_OF_USE_CONTENT);
        assertThat(entity.getVersion()).isEqualTo(TEST_TERMS_OF_USE_VERSION);
    }

    @Test
    @DisplayName("toTerm으로 약관 반환")
    void testToTerm_givenValidTermEntity_willReturnTerm() {
        assertThat(termJpaMapper.toTerm(createTermsOfUseEntityWithUuid())).isEqualTo(createTerm());
    }
}
