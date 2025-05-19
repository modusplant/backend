package kr.modusplant.domains.term.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.common.context.DomainsControllerOnlyContext;
import kr.modusplant.domains.term.app.http.response.TermResponse;
import kr.modusplant.domains.term.app.service.TermApplicationService;
import kr.modusplant.domains.term.common.util.app.http.response.TermResponseTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static kr.modusplant.global.vo.CamelCaseWord.DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DomainsControllerOnlyContext
class TermControllerTest implements TermResponseTestUtils {

    private final MockMvc mockMvc;

    @Autowired
    private final TermApplicationService termApplicationService;

    @Autowired
    TermControllerTest(MockMvc mockMvc, TermApplicationService termApplicationService) {
        this.mockMvc = mockMvc;
        this.termApplicationService = termApplicationService;
    }

    @DisplayName("모든 약관 얻기")
    @Test
    void getAllTermsTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        List<TermResponse> termsOfUseResponseList = List.of(termsOfUseResponse);

        when(termApplicationService.getAll()).thenReturn(termsOfUseResponseList);

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/crud/terms"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<List<TermResponse>>() {
                })
        ).isEqualTo(termsOfUseResponseList);
    }
}