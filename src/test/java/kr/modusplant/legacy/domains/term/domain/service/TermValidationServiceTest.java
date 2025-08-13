package kr.modusplant.legacy.domains.term.domain.service;

import kr.modusplant.legacy.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.legacy.domains.term.common.util.app.http.response.TermResponseTestUtils;
import kr.modusplant.legacy.domains.term.common.util.entity.TermEntityTestUtils;
import kr.modusplant.legacy.domains.term.error.TermExistsException;
import kr.modusplant.legacy.domains.term.error.TermNotFoundException;
import kr.modusplant.legacy.domains.term.persistence.entity.TermEntity;
import kr.modusplant.legacy.domains.term.persistence.repository.TermRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
class TermValidationServiceTest implements TermResponseTestUtils, TermEntityTestUtils {

    private final TermValidationService termValidationService;
    private final TermRepository termRepository;

    @Autowired
    TermValidationServiceTest(TermValidationService termValidationService, TermRepository termRepository) {
        this.termValidationService = termValidationService;
        this.termRepository = termRepository;
    }

    @DisplayName("존재하는 약관 UUID 검증")
    @Test
    void validateExistedUuidTest() {
        // given
        TermEntity termEntity = createTermsOfUseEntityWithUuid();
        UUID termEntityUuid = termEntity.getUuid();

        // when
        given(termRepository.existsByUuid(termEntityUuid)).willReturn(true);

        // then
        TermExistsException existsException = assertThrows(TermExistsException.class,
                () -> termValidationService.validateExistedUuid(termEntityUuid));
        assertThat(existsException.getMessage()).isEqualTo(new TermExistsException().getMessage());
    }

    @DisplayName("존재하는 약관 이름 검증")
    @Test
    void validateExistedTermNameTest() {
        // given
        TermEntity termEntity = createTermsOfUseEntityWithUuid();
        UUID termEntityUuid = termEntity.getUuid();

        // when
        given(termRepository.findByUuid(termEntityUuid)).willReturn(Optional.empty());
        given(termRepository.existsByName(termEntity.getName())).willReturn(true);

        // then
        TermExistsException existsException = assertThrows(TermExistsException.class,
                () -> termValidationService.validateExistedName(termEntity.getName()));
        assertThat(existsException.getMessage()).isEqualTo(new TermExistsException().getMessage());
    }

    @DisplayName("존재하지 않는 약관 UUID 검증")
    @Test
    void validateNotFoundUuidTest() {
        // given & when
        TermEntity termEntity = createTermsOfUseEntityWithUuid();
        UUID termEntityUuid = termEntity.getUuid();

        given(termRepository.existsByUuid(termEntityUuid)).willReturn(false);

        // then
        TermNotFoundException existsException = assertThrows(TermNotFoundException.class,
                () -> termValidationService.validateNotFoundUuid(termEntityUuid));
        assertThat(existsException.getMessage()).isEqualTo(new TermNotFoundException().getMessage());
    }
}