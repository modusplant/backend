package kr.modusplant.domains.term.framework.in.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.term.adaptor.controller.SiteMemberTermController;
import kr.modusplant.domains.term.usecase.response.SiteMemberTermResponse;
import kr.modusplant.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static kr.modusplant.domains.term.common.util.usecase.request.SiteMemberTermCreateRequestTestUtils.testSiteMemberTermCreateRequest;
import static kr.modusplant.domains.term.common.util.usecase.request.SiteMemberTermUpdateRequestTestUtils.testSiteMemberTermUpdateRequest;
import static kr.modusplant.domains.term.common.util.usecase.response.SiteMemberTermResponseTestUtils.testSiteMemberTermResponse;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.MEMBER_TERM_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

class SiteMemberTermRestControllerTest {
    private final SiteMemberTermController siteMemberTermController = Mockito.mock(SiteMemberTermController.class);
    private final SiteMemberTermRestController siteMemberTermRestController = new SiteMemberTermRestController(siteMemberTermController);

    @BeforeAll
    static void setupObjectMapper() {
        new ObjectMapperHolder(new ObjectMapper());
    }

    @Test
    @DisplayName("registerSiteMemberTerm으로 사이트 회원 약관 등록 응답 반환")
    void testRegisterSiteMemberTerm_givenValidRequest_willReturnResponse() {
        // given
        given(siteMemberTermController.register(testSiteMemberTermCreateRequest)).willReturn(testSiteMemberTermResponse);

        // when
        ResponseEntity<DataResponse<SiteMemberTermResponse>> responseEntity =
                siteMemberTermRestController.registerSiteMemberTerm(testSiteMemberTermCreateRequest);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok(testSiteMemberTermResponse).toString());
    }

    @Test
    @DisplayName("updateSiteMemberTerm으로 사이트 회원 약관 수정 응답 반환")
    void testUpdateSiteMemberTerm_givenValidRequest_willReturnResponse() {
        // given
        given(siteMemberTermController.update(testSiteMemberTermUpdateRequest)).willReturn(testSiteMemberTermResponse);

        // when
        ResponseEntity<DataResponse<SiteMemberTermResponse>> responseEntity =
                siteMemberTermRestController.updateSiteMemberTerm(testSiteMemberTermUpdateRequest);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok(testSiteMemberTermResponse).toString());
    }

    @Test
    @DisplayName("deleteSiteMemberTerm으로 사이트 회원 약관 삭제 응답 반환")
    void testDeleteSiteMemberTerm_givenValidUuid_willReturnResponse() {
        // given
        willDoNothing().given(siteMemberTermController).delete(any());

        // when
        ResponseEntity<DataResponse<Void>> responseEntity =
                siteMemberTermRestController.deleteSiteMemberTerm(MEMBER_TERM_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("getSiteMemberTerm으로 사이트 회원 약관 조회 응답 반환")
    void testGetSiteMemberTerm_givenValidUuid_willReturnResponse() {
        // given
        given(siteMemberTermController.getSiteMemberTerm(any())).willReturn(testSiteMemberTermResponse);

        // when
        ResponseEntity<DataResponse<SiteMemberTermResponse>> responseEntity =
                siteMemberTermRestController.getSiteMemberTerm(MEMBER_TERM_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok(testSiteMemberTermResponse).toString());
    }

    @Test
    @DisplayName("getSiteMemberTermList로 사이트 회원 약관 목록 조회 응답 반환")
    void testGetSiteMemberTermList_willReturnResponse() {
        // given
        given(siteMemberTermController.getSiteMemberTermList()).willReturn(List.of(testSiteMemberTermResponse));

        // when
        ResponseEntity<DataResponse<List<SiteMemberTermResponse>>> responseEntity =
                siteMemberTermRestController.getSiteMemberTermList();

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok(List.of(testSiteMemberTermResponse)).toString());
    }
}
