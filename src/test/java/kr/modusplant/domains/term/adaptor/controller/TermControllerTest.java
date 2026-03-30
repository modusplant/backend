package kr.modusplant.domains.term.adaptor.controller;

import kr.modusplant.domains.term.adaptor.mapper.TermMapperImpl;
import kr.modusplant.domains.term.common.util.domain.aggregate.TermTestUtils;
import kr.modusplant.domains.term.domain.exception.TermNotFoundException;
import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.domains.term.usecase.port.mapper.TermMapper;
import kr.modusplant.domains.term.usecase.port.repository.TermRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static kr.modusplant.domains.term.common.util.domain.vo.TermIdTestUtils.testTermId;
import static kr.modusplant.domains.term.common.util.usecase.request.TermCreateRequestTestUtils.testTermCreateRequest;
import static kr.modusplant.domains.term.common.util.usecase.request.TermUpdateRequestTestUtils.testTermUpdateRequest;
import static kr.modusplant.domains.term.common.util.usecase.response.TermResponseTestUtils.testTermResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

class TermControllerTest implements TermTestUtils {
    private final TermMapper termMapper = new TermMapperImpl();
    private final TermRepository termRepository = Mockito.mock(TermRepository.class);
    private final TermController termController = new TermController(termMapper, termRepository);

    @Test
    @DisplayName("register로 약관 등록")
    void testRegister_givenValidRequest_willReturnResponse() {
        // given
        given(termRepository.save(any())).willReturn(createTerm());

        // when & then
        assertThat(termController.register(testTermCreateRequest)).isEqualTo(testTermResponse);
    }

    @Nested
    @DisplayName("update로 약관 수정")
    class UpdateTermTest {
        @Test
        @DisplayName("존재하는 약관으로 update 성공")
        void testUpdate_givenValidRequest_willReturnResponse() {
            // given
            given(termRepository.findById(any())).willReturn(Optional.of(createTerm()));
            given(termRepository.save(any())).willReturn(createTerm());

            // when & then
            assertThat(termController.update(testTermUpdateRequest)).isEqualTo(testTermResponse);
        }

        @Test
        @DisplayName("존재하지 않는 약관으로 update 시 오류 발생")
        void testUpdate_givenNotFoundTermId_willThrowException() {
            // given
            given(termRepository.findById(any())).willReturn(Optional.empty());

            // when
            TermNotFoundException exception = assertThrows(TermNotFoundException.class,
                    () -> termController.update(testTermUpdateRequest));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.TERM_NOT_FOUND);
        }
    }

    @Test
    @DisplayName("delete로 약관 삭제")
    void testDelete_givenValidTermId_willDelete() {
        // given
        willDoNothing().given(termRepository).deleteById(any());

        // when & then (예외 없이 실행됨을 확인)
        termController.delete(testTermId);
    }

    @Nested
    @DisplayName("getTerm으로 약관 조회")
    class GetTermTest {
        @Test
        @DisplayName("존재하는 약관 조회")
        void testGetTerm_givenValidTermId_willReturnResponse() {
            // given
            given(termRepository.findById(any())).willReturn(Optional.of(createTerm()));

            // when & then
            assertThat(termController.getTerm(testTermId)).isEqualTo(testTermResponse);
        }

        @Test
        @DisplayName("존재하지 않는 약관 조회 시 오류 발생")
        void testGetTerm_givenNotFoundTermId_willThrowException() {
            // given
            given(termRepository.findById(any())).willReturn(Optional.empty());

            // when
            TermNotFoundException exception = assertThrows(TermNotFoundException.class,
                    () -> termController.getTerm(testTermId));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.TERM_NOT_FOUND);
        }
    }

    @Test
    @DisplayName("getTermList로 약관 목록 조회")
    void testGetTermList_givenTermsExist_willReturnResponseList() {
        // given
        given(termRepository.findAll()).willReturn(List.of(createTerm()));

        // when & then
        assertThat(termController.getTermList()).containsExactly(testTermResponse);
    }
}
