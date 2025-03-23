package kr.modusplant.global.persistence.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.global.domain.model.Term;
import kr.modusplant.global.domain.service.crud.TermService;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.global.mapper.TermEntityMapper;
import kr.modusplant.global.mapper.TermEntityMapperImpl;
import kr.modusplant.global.persistence.entity.TermEntity;
import kr.modusplant.global.persistence.repository.TermJpaRepository;
import kr.modusplant.support.context.ServiceOnlyContext;
import kr.modusplant.support.util.domain.TermTestUtils;
import kr.modusplant.support.util.entity.TermEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.CamelCaseWord.NAME;
import static kr.modusplant.global.vo.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.vo.ExceptionMessage.NOT_FOUND_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ServiceOnlyContext
class TermServiceImplTest implements TermTestUtils, TermEntityTestUtils {

    private final TermService termService;
    private final TermJpaRepository termRepository;
    private final TermEntityMapper termMapper = new TermEntityMapperImpl();

    @Autowired
    TermServiceImplTest(TermService termService, TermJpaRepository termRepository) {
        this.termService = termService;
        this.termRepository = termRepository;
    }

    @DisplayName("uuid로 약관 얻기")
    @Test
    void getByUuidTest() {
        // given
        TermEntity termEntity = createTermsOfUseEntity();
        Term term = termMapper.toTerm(termEntity);

        given(termRepository.findByUuid(termEntity.getUuid())).willReturn(Optional.of(termEntity));
        given(termRepository.findByName(termEntity.getName())).willReturn(Optional.empty());
        given(termRepository.save(termEntity)).willReturn(termEntity);

        // when
        term = termService.insert(term);

        // then
        assertThat(termService.getByUuid(term.getUuid()).orElseThrow()).isEqualTo(term);
    }

    @DisplayName("name으로 약관 얻기")
    @Test
    void getByNameTest() {
        // given
        TermEntity termEntity = createTermsOfUseEntity();
        Term term = termMapper.toTerm(termEntity);

        given(termRepository.findByUuid(termEntity.getUuid())).willReturn(Optional.empty());
        given(termRepository.findByName(termEntity.getName())).willReturn(Optional.empty()).willReturn(Optional.of(termEntity));
        given(termRepository.save(termEntity)).willReturn(termEntity);

        // when
        term = termService.insert(term);

        // then
        assertThat(termService.getByName(termEntity.getName()).orElseThrow()).isEqualTo(term);
    }

    @DisplayName("version으로 약관 얻기")
    @Test
    void getByVersionTest() {
        // given
        TermEntity termEntity = createTermsOfUseEntity();
        Term term = termMapper.toTerm(termEntity);

        given(termRepository.findByUuid(termEntity.getUuid())).willReturn(Optional.empty());
        given(termRepository.findByName(termEntity.getName())).willReturn(Optional.empty());
        given(termRepository.save(termEntity)).willReturn(termEntity);
        given(termRepository.findByVersion(termEntity.getVersion())).willReturn(List.of(termEntity));

        // when
        term = termService.insert(term);

        // then
        assertThat(termService.getByVersion(termEntity.getVersion()).getFirst()).isEqualTo(term);
    }

    @DisplayName("약관 삽입 간 검증")
    @Test
    void validateDuringInsertTest() {
        // given
        TermEntity termEntity = createTermsOfUseEntityWithUuid();
        UUID termEntityUuid = termEntity.getUuid();
        Term term = termMapper.toTerm(termEntity);

        // Existed uuid 검증
        // given & when
        given(termRepository.findByUuid(termEntityUuid)).willReturn(Optional.of(termEntity));

        // then
        EntityExistsException existsException = assertThrows(EntityExistsWithUuidException.class,
                () -> termService.insert(term));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY, "uuid", termEntityUuid, TermEntity.class));

        // Existed name 검증
        // given & when
        given(termRepository.findByUuid(termEntityUuid)).willReturn(Optional.empty());
        given(termRepository.findByName(termEntity.getName())).willReturn(Optional.of(termEntity));

        // then
        existsException = assertThrows(EntityExistsException.class, () -> termService.insert(term));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY, NAME, termEntity.getName(), TermEntity.class));
    }

    @DisplayName("약관 갱신")
    @Test
    void updateTest() {
        // given
        String updatedContent = "갱신된 컨텐츠";
        TermEntity termEntity = createTermsOfUseEntityWithUuid();
        Term term = termMapper.toTerm(termEntity);
        TermEntity updatedTermEntity = TermEntity.builder().termEntity(termEntity).content(updatedContent).build();
        Term updatedTerm = termMapper.toTerm(updatedTermEntity);

        given(termRepository.findByUuid(termEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(termEntity));
        given(termRepository.save(termEntity)).willReturn(termEntity).willReturn(updatedTermEntity);
        given(termRepository.findByName(updatedTermEntity.getName())).willReturn(Optional.empty()).willReturn(Optional.of(updatedTermEntity));

        // when
        termService.insert(term);
        termService.update(updatedTerm);

        // then
        assertThat(termService.getByName(updatedTermEntity.getName()).orElseThrow().getContent()).isEqualTo(updatedContent);
    }

    @DisplayName("약관 갱신 간 검증")
    @Test
    void validateDuringUpdateTest() {
        // given & when
        TermEntity termEntity = createTermsOfUseEntityWithUuid();
        UUID termEntityUuid = termEntity.getUuid();
        Term term = termMapper.toTerm(termEntity);

        given(termRepository.findByUuid(termEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundException existsException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> termService.update(term));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", termEntityUuid, TermEntity.class));
    }

    @DisplayName("uuid로 약관 제거")
    @Test
    void removeByUuidTest() {
        // given
        TermEntity termEntity = createTermsOfUseEntityWithUuid();
        Term term = termMapper.toTerm(termEntity);

        given(termRepository.findByUuid(term.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(termEntity));
        given(termRepository.findByName(termEntity.getName())).willReturn(Optional.empty());
        given(termRepository.save(createTermsOfUseEntity())).willReturn(termEntity);
        given(termRepository.findAll()).willReturn(emptyList());
        willDoNothing().given(termRepository).deleteByUuid(term.getUuid());

        // when
        term = termService.insert(term);
        termService.removeByUuid(term.getUuid());

        // then
        assertThat(termService.getAll()).isEmpty();
    }

    @DisplayName("uuid로 약관 제거 간 검증")
    @Test
    void validateDuringRemoveByUuidTest() {
        // given & when
        TermEntity termEntity = createTermsOfUseEntityWithUuid();
        UUID termEntityUuid = termEntity.getUuid();

        given(termRepository.findByUuid(termEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundException existsException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> termService.removeByUuid(termEntityUuid));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", termEntityUuid, TermEntity.class));
    }
}