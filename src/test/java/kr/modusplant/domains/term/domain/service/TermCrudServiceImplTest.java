package kr.modusplant.domains.term.domain.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.common.context.CrudServiceOnlyContext;
import kr.modusplant.domains.term.common.util.domain.TermTestUtils;
import kr.modusplant.domains.term.common.util.entity.TermEntityTestUtils;
import kr.modusplant.domains.term.domain.model.Term;
import kr.modusplant.domains.term.domain.service.supers.TermCrudService;
import kr.modusplant.domains.term.mapper.TermEntityMapper;
import kr.modusplant.domains.term.mapper.TermEntityMapperImpl;
import kr.modusplant.domains.term.persistence.entity.TermEntity;
import kr.modusplant.domains.term.persistence.repository.TermCrudJpaRepository;
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
class TermCrudServiceImplTest implements TermTestUtils, TermEntityTestUtils {

    private final TermCrudService termCrudService;
    private final TermCrudJpaRepository termRepository;
    private final TermEntityMapper termMapper = new TermEntityMapperImpl();

    @Autowired
    TermCrudServiceImplTest(TermCrudService termCrudService, TermCrudJpaRepository termRepository) {
        this.termCrudService = termCrudService;
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
        term = termCrudService.insert(term);

        // then
        assertThat(termCrudService.getByUuid(term.getUuid()).orElseThrow()).isEqualTo(term);
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
        term = termCrudService.insert(term);

        // then
        assertThat(termCrudService.getByName(termEntity.getName()).orElseThrow()).isEqualTo(term);
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
        term = termCrudService.insert(term);

        // then
        assertThat(termCrudService.getByVersion(termEntity.getVersion()).getFirst()).isEqualTo(term);
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
        assertThat(termCrudService.getByUuid(uuid)).isEmpty();

        // getByName
        // given & when
        given(termRepository.findByName(name)).willReturn(Optional.empty());

        // then
        assertThat(termCrudService.getByName(name)).isEmpty();
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
                () -> termCrudService.insert(term));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY, "uuid", termEntityUuid, TermEntity.class));

        // Existed name 검증
        // given & when
        given(termRepository.findByUuid(termEntityUuid)).willReturn(Optional.empty());
        given(termRepository.findByName(termEntity.getName())).willReturn(Optional.of(termEntity));

        // then
        existsException = assertThrows(EntityExistsException.class, () -> termCrudService.insert(term));
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
        termCrudService.insert(term);
        termCrudService.update(updatedTerm);

        // then
        assertThat(termCrudService.getByName(updatedTermEntity.getName()).orElseThrow().getContent()).isEqualTo(updatedContent);
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
                () -> termCrudService.update(term));
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
        termCrudService.insert(term);
        termCrudService.removeByUuid(uuid);

        // then
        assertThat(termCrudService.getByUuid(uuid)).isEmpty();
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
                () -> termCrudService.removeByUuid(termEntityUuid));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", termEntityUuid, TermEntity.class));
    }
}