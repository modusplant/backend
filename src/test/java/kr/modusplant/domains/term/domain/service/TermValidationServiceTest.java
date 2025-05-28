package kr.modusplant.domains.term.domain.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.term.common.util.app.http.response.TermResponseTestUtils;
import kr.modusplant.domains.term.common.util.entity.TermEntityTestUtils;
import kr.modusplant.domains.term.persistence.entity.TermEntity;
import kr.modusplant.domains.term.persistence.repository.TermRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.global.enums.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.enums.ExceptionMessage.NOT_FOUND_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.CamelCaseWord.NAME;
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
        given(termRepository.findByUuid(termEntityUuid)).willReturn(Optional.of(termEntity));

        // then
        EntityExistsException existsException = assertThrows(EntityExistsWithUuidException.class,
                () -> termValidationService.validateExistedUuid(termEntityUuid));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY.getValue(), "uuid", termEntityUuid, TermEntity.class));
    }

    @DisplayName("존재하는 약관 이름 검증")
    @Test
    void validateExistedTermNameTest() {
        // given
        TermEntity termEntity = createTermsOfUseEntityWithUuid();
        UUID termEntityUuid = termEntity.getUuid();

        // when
        given(termRepository.findByUuid(termEntityUuid)).willReturn(Optional.empty());
        given(termRepository.findByName(termEntity.getName())).willReturn(Optional.of(termEntity));

        // then
        EntityExistsException existsException = assertThrows(EntityExistsException.class,
                () -> termValidationService.validateExistedName(termEntity.getName()));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY.getValue(), NAME, termEntity.getName(), TermEntity.class));
    }

    @DisplayName("존재하지 않는 약관 UUID 검증")
    @Test
    void validateNotFoundUuidTest() {
        // given & when
        TermEntity termEntity = createTermsOfUseEntityWithUuid();
        UUID termEntityUuid = termEntity.getUuid();

        given(termRepository.findByUuid(termEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundException existsException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> termValidationService.validateNotFoundUuid(termEntityUuid));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY.getValue(), "uuid", termEntityUuid, TermEntity.class));
    }
}