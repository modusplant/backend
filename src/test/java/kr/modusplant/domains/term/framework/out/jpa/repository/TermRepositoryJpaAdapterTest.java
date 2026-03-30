package kr.modusplant.domains.term.framework.out.jpa.repository;

import kr.modusplant.domains.term.common.util.domain.aggregate.TermTestUtils;
import kr.modusplant.domains.term.domain.aggregate.Term;
import kr.modusplant.domains.term.domain.exception.TermNotFoundException;
import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.domains.term.domain.vo.TermContent;
import kr.modusplant.domains.term.domain.vo.TermName;
import kr.modusplant.domains.term.domain.vo.TermVersion;
import kr.modusplant.domains.term.framework.out.jpa.mapper.TermJpaMapperImpl;
import kr.modusplant.framework.jpa.entity.TermEntity;
import kr.modusplant.framework.jpa.entity.common.util.TermEntityTestUtils;
import kr.modusplant.framework.jpa.repository.TermJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static kr.modusplant.domains.term.common.util.domain.vo.TermIdTestUtils.testTermId;
import static kr.modusplant.shared.persistence.common.util.constant.TermConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

class TermRepositoryJpaAdapterTest implements TermTestUtils, TermEntityTestUtils {
    private final TermJpaMapperImpl termJpaMapper = new TermJpaMapperImpl();
    private final TermJpaRepository termJpaRepository = Mockito.mock(TermJpaRepository.class);
    private final TermRepositoryJpaAdapter termRepositoryJpaAdapter = new TermRepositoryJpaAdapter(termJpaMapper, termJpaRepository);

    @Test
    @DisplayName("save로 새로운 약관 저장")
    void testSave_givenNewTerm_willReturnSavedTerm() {
        // given
        TermEntity savedEntity = createTermsOfUseEntityWithUuid();
        given(termJpaRepository.save(any())).willReturn(savedEntity);

        Term newTerm = Term.create(
                TermName.create(TEST_TERMS_OF_USE_NAME),
                TermContent.create(TEST_TERMS_OF_USE_CONTENT),
                TermVersion.create(TEST_TERMS_OF_USE_VERSION)
        );

        // when & then
        assertThat(termRepositoryJpaAdapter.save(newTerm)).isEqualTo(createTerm());
    }

    @Test
    @DisplayName("save로 기존 약관 내용 수정")
    void testSave_givenExistingTerm_willUpdateContent() {
        // given
        TermEntity existingEntity = createTermsOfUseEntityWithUuid();
        given(termJpaRepository.findById(TEST_TERMS_OF_USE_UUID)).willReturn(Optional.of(existingEntity));

        Term updatedTerm = Term.create(
                testTermId,
                createTerm().getTermName(),
                TermContent.create("수정된 약관 내용"),
                createTerm().getTermVersion()
        );

        // when
        Term result = termRepositoryJpaAdapter.save(updatedTerm);

        // then
        assertThat(result.getTermContent().getValue()).isEqualTo("수정된 약관 내용");
    }

    @Test
    @DisplayName("save 시 존재하지 않는 약관 수정 시 오류 발생")
    void testSave_givenNotFoundExistingTerm_willThrowException() {
        // given
        given(termJpaRepository.findById(TEST_TERMS_OF_USE_UUID)).willReturn(Optional.empty());

        // when
        TermNotFoundException exception = assertThrows(TermNotFoundException.class,
                () -> termRepositoryJpaAdapter.save(createTerm()));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.TERM_NOT_FOUND);
    }

    @Test
    @DisplayName("findById로 약관 반환(존재할 때)")
    void testFindById_givenValidTermId_willReturnOptionalTerm() {
        // given
        given(termJpaRepository.findById(TEST_TERMS_OF_USE_UUID)).willReturn(Optional.of(createTermsOfUseEntityWithUuid()));

        // when & then
        assertThat(termRepositoryJpaAdapter.findById(testTermId)).isEqualTo(Optional.of(createTerm()));
    }

    @Test
    @DisplayName("findById로 빈 Optional 반환(존재하지 않을 때)")
    void testFindById_givenNotFoundTermId_willReturnOptionalEmpty() {
        // given
        given(termJpaRepository.findById(TEST_TERMS_OF_USE_UUID)).willReturn(Optional.empty());

        // when & then
        assertThat(termRepositoryJpaAdapter.findById(testTermId)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("findAll로 약관 목록 반환")
    void testFindAll_willReturnTermList() {
        // given
        given(termJpaRepository.findAll()).willReturn(List.of(createTermsOfUseEntityWithUuid()));

        // when & then
        assertThat(termRepositoryJpaAdapter.findAll()).containsExactly(createTerm());
    }

    @Test
    @DisplayName("isIdExist로 true 반환")
    void testIsIdExist_givenIdThatExists_willReturnTrue() {
        // given
        given(termJpaRepository.existsById(TEST_TERMS_OF_USE_UUID)).willReturn(true);

        // when & then
        assertThat(termRepositoryJpaAdapter.isIdExist(testTermId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isIdExist로 false 반환")
    void testIsIdExist_givenIdThatIsNotExist_willReturnFalse() {
        // given
        given(termJpaRepository.existsById(TEST_TERMS_OF_USE_UUID)).willReturn(false);

        // when & then
        assertThat(termRepositoryJpaAdapter.isIdExist(testTermId)).isEqualTo(false);
    }

    @Test
    @DisplayName("deleteById로 약관 삭제")
    void testDeleteById_givenValidTermId_willDelete() {
        // given
        willDoNothing().given(termJpaRepository).deleteById(TEST_TERMS_OF_USE_UUID);

        // when & then (예외 없이 실행됨을 확인)
        termRepositoryJpaAdapter.deleteById(testTermId);
    }
}
