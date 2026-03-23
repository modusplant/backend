package kr.modusplant.domains.term.adaptor.controller;

import kr.modusplant.domains.term.adaptor.mapper.SiteMemberTermMapperImpl;
import kr.modusplant.domains.term.common.util.domain.aggregate.SiteMemberTermTestUtils;
import kr.modusplant.domains.term.domain.exception.AlreadySiteMemberTermException;
import kr.modusplant.domains.term.domain.exception.SiteMemberNotFoundException;
import kr.modusplant.domains.term.domain.exception.SiteMemberTermNotFoundException;
import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.domains.term.usecase.port.mapper.SiteMemberTermMapper;
import kr.modusplant.domains.term.usecase.port.repository.SiteMemberTermRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static kr.modusplant.domains.term.common.util.domain.vo.SiteMemberTermIdTestUtils.testSiteMemberTermId;
import static kr.modusplant.domains.term.common.util.usecase.request.SiteMemberTermCreateRequestTestUtils.testSiteMemberTermCreateRequest;
import static kr.modusplant.domains.term.common.util.usecase.request.SiteMemberTermUpdateRequestTestUtils.testSiteMemberTermUpdateRequest;
import static kr.modusplant.domains.term.common.util.usecase.response.SiteMemberTermResponseTestUtils.testSiteMemberTermResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

class SiteMemberTermControllerTest implements SiteMemberTermTestUtils {
    private final SiteMemberTermMapper siteMemberTermMapper = new SiteMemberTermMapperImpl();
    private final SiteMemberTermRepository siteMemberTermRepository = Mockito.mock(SiteMemberTermRepository.class);
    private final SiteMemberTermController siteMemberTermController = new SiteMemberTermController(siteMemberTermMapper, siteMemberTermRepository);

    @Nested
    @DisplayName("register로 사이트 회원 약관 등록")
    class RegisterSiteMemberTermTest {
        @Test
        @DisplayName("중복되지 않는 회원 아이디로 register 성공")
        void testRegister_givenValidRequest_willReturnResponse() {
            // given
            given(siteMemberTermRepository.isIdExist(any())).willReturn(false);
            given(siteMemberTermRepository.save(any())).willReturn(createSiteMemberTerm());

            // when & then
            assertThat(siteMemberTermController.register(testSiteMemberTermCreateRequest)).isEqualTo(testSiteMemberTermResponse);
        }

        @Test
        @DisplayName("이미 등록된 회원 약관으로 register 시 오류 발생")
        void testRegister_givenAlreadyExistedSiteMemberTerm_willThrowException() {
            // given
            given(siteMemberTermRepository.isIdExist(any())).willReturn(true);

            // when
            AlreadySiteMemberTermException exception = assertThrows(AlreadySiteMemberTermException.class,
                    () -> siteMemberTermController.register(testSiteMemberTermCreateRequest));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.ALREADY_SITE_MEMBER_TERM);
        }
    }

    @Nested
    @DisplayName("update로 사이트 회원 약관 수정")
    class UpdateSiteMemberTermTest {
        @Test
        @DisplayName("존재하는 회원 약관으로 update 성공")
        void testUpdate_givenValidRequest_willReturnResponse() {
            // given
            given(siteMemberTermRepository.findById(any())).willReturn(Optional.of(createSiteMemberTerm()));
            given(siteMemberTermRepository.save(any())).willReturn(createSiteMemberTerm());

            // when & then
            assertThat(siteMemberTermController.update(testSiteMemberTermUpdateRequest)).isEqualTo(testSiteMemberTermResponse);
        }

        @Test
        @DisplayName("존재하지 않는 회원 아이디로 update 시 오류 발생")
        void testUpdate_givenNotFoundSiteMemberTermId_willThrowException() {
            // given
            given(siteMemberTermRepository.findById(any())).willReturn(Optional.empty());

            // when
            SiteMemberNotFoundException exception = assertThrows(SiteMemberNotFoundException.class,
                    () -> siteMemberTermController.update(testSiteMemberTermUpdateRequest));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.SITE_MEMBER_NOT_FOUND);
        }
    }

    @Test
    @DisplayName("delete로 사이트 회원 약관 삭제")
    void testDelete_givenValidSiteMemberTermId_willDelete() {
        // given
        willDoNothing().given(siteMemberTermRepository).deleteById(any());

        // when & then (예외 없이 실행됨을 확인)
        siteMemberTermController.delete(testSiteMemberTermId);
    }

    @Nested
    @DisplayName("getSiteMemberTerm으로 사이트 회원 약관 조회")
    class GetSiteMemberTermTest {
        @Test
        @DisplayName("존재하는 사이트 회원 약관 조회")
        void testGetSiteMemberTerm_givenValidId_willReturnResponse() {
            // given
            given(siteMemberTermRepository.findById(any())).willReturn(Optional.of(createSiteMemberTerm()));

            // when & then
            assertThat(siteMemberTermController.getSiteMemberTerm(testSiteMemberTermId)).isEqualTo(testSiteMemberTermResponse);
        }

        @Test
        @DisplayName("존재하지 않는 사이트 회원 약관 조회 시 오류 발생")
        void testGetSiteMemberTerm_givenNotFoundId_willThrowException() {
            // given
            given(siteMemberTermRepository.findById(any())).willReturn(Optional.empty());

            // when
            SiteMemberTermNotFoundException exception = assertThrows(SiteMemberTermNotFoundException.class,
                    () -> siteMemberTermController.getSiteMemberTerm(testSiteMemberTermId));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.SITE_MEMBER_TERM_NOT_FOUND);
        }
    }

    @Test
    @DisplayName("getSiteMemberTermList로 사이트 회원 약관 목록 조회")
    void testGetSiteMemberTermList_givenTermsExist_willReturnResponseList() {
        // given
        given(siteMemberTermRepository.findAll()).willReturn(List.of(createSiteMemberTerm()));

        // when & then
        assertThat(siteMemberTermController.getSiteMemberTermList()).containsExactly(testSiteMemberTermResponse);
    }
}
