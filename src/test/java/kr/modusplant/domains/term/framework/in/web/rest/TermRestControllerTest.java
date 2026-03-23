package kr.modusplant.domains.term.framework.in.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.term.adaptor.controller.TermController;
import kr.modusplant.domains.term.usecase.response.TermResponse;
import kr.modusplant.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static kr.modusplant.domains.term.common.util.usecase.response.TermResponseTestUtils.testTermResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class TermRestControllerTest {
    private final TermController termController = Mockito.mock(TermController.class);
    private final TermRestController termRestController = new TermRestController(termController);

    @BeforeAll
    static void setupObjectMapper() {
        new ObjectMapperHolder(new ObjectMapper());
    }

    @Test
    @DisplayName("getTermList로 약관 목록 응답 반환")
    void testGetTermList_willReturnResponse() {
        // given
        given(termController.getTermList()).willReturn(List.of(testTermResponse));

        // when
        ResponseEntity<DataResponse<List<TermResponse>>> responseEntity = termRestController.getTermList();

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok(List.of(testTermResponse)).toString());
    }
}
