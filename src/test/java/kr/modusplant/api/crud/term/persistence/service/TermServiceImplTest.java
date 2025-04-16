package kr.modusplant.api.crud.term.persistence.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.api.crud.common.context.CrudServiceOnlyContext;
import kr.modusplant.api.crud.term.common.util.domain.TermTestUtils;
import kr.modusplant.api.crud.term.common.util.entity.TermEntityTestUtils;
import kr.modusplant.api.crud.term.domain.model.Term;
import kr.modusplant.api.crud.term.domain.service.supers.TermService;
import kr.modusplant.api.crud.term.mapper.TermEntityMapper;
import kr.modusplant.api.crud.term.mapper.TermEntityMapperImpl;
import kr.modusplant.api.crud.term.persistence.entity.TermEntity;
import kr.modusplant.api.crud.term.persistence.repository.TermJpaRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.CamelCaseWord.NAME;
import static kr.modusplant.global.vo.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.vo.ExceptionMessage.NOT_FOUND_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@CrudServiceOnlyContext
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

    @DisplayName("빈 약관 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        TermEntity termEntity = createTermsOfUseEntity();
        UUID uuid = termEntity.getUuid();
        String name = termEntity.getName();

        // getByUuid
        // given & when
        given(termRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // then
        assertThat(termService.getByUuid(uuid)).isEmpty();

        // getByName
        // given & when
        given(termRepository.findByName(name)).willReturn(Optional.empty());

        // then
        assertThat(termService.getByName(name)).isEmpty();
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
        UUID uuid = term.getUuid();

        given(termRepository.findByUuid(uuid)).willReturn(Optional.empty()).willReturn(Optional.of(termEntity)).willReturn(Optional.empty());
        given(termRepository.findByName(termEntity.getName())).willReturn(Optional.empty());
        given(termRepository.save(createTermsOfUseEntity())).willReturn(termEntity);
        willDoNothing().given(termRepository).deleteByUuid(uuid);

        // when
        termService.insert(term);
        termService.removeByUuid(uuid);

        // then
        assertThat(termService.getByUuid(uuid)).isEmpty();
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