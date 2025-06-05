package kr.modusplant.domains.term.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.common.context.DomainsControllerOnlyContext;
import kr.modusplant.domains.term.app.http.response.TermResponse;
import kr.modusplant.domains.term.app.service.TermApplicationService;
import kr.modusplant.domains.term.common.util.app.http.request.TermRequestTestUtils;
import kr.modusplant.domains.term.common.util.app.http.response.TermResponseTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DomainsControllerOnlyContext
class TermControllerTest implements TermRequestTestUtils, TermResponseTestUtils {

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
                mockMvc.perform(get("/api/v1/terms"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<List<TermResponse>>() {
                })
        ).isEqualTo(termsOfUseResponseList);
    }

    @DisplayName("버전으로 약관 얻기")
    @Test
    void getTermsByVersionTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        List<TermResponse> termsOfUseResponseList = List.of(termsOfUseResponse);
        String version = termsOfUse.getVersion();

        when(termApplicationService.getByVersion(version)).thenReturn(termsOfUseResponseList);

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/terms/version/{version}", version))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<List<TermResponse>>() {
                })
        ).isEqualTo(termsOfUseResponseList);
    }

    @DisplayName("UUID로 약관 얻기")
    @Test
    void getTermByUuidTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        UUID uuid = termsOfUseWithUuid.getUuid();

        when(termApplicationService.getByUuid(uuid)).thenReturn(Optional.of(termsOfUseResponse));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/terms/{uuid}", uuid))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<TermResponse>() {
                })
        ).isEqualTo(termsOfUseResponse);
    }

    @DisplayName("이름으로 약관 얻기")
    @Test
    void getTermByNameTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        String name = termsOfUse.getName();

        when(termApplicationService.getByName(name)).thenReturn(Optional.of(termsOfUseResponse));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/terms/name/{name}", name))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<TermResponse>() {
                })
        ).isEqualTo(termsOfUseResponse);
    }

    @DisplayName("빈 약관 얻기")
    @Test
    void getEmptyTermTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        UUID uuid = termsOfUseWithUuid.getUuid();
        String name = termsOfUse.getName();

        when(termApplicationService.getByUuid(uuid)).thenReturn(Optional.empty());
        when(termApplicationService.getByName(name)).thenReturn(Optional.empty());

        // uuid - when
        Map<String, Object> uuidResponseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/terms/{uuid}", uuid))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // uuid - then
        assertThat(objectMapper.convertValue(uuidResponseMap.get(DATA),
                new TypeReference<TermResponse>() {
                })
        ).isEqualTo(null);

        // name - when
        Map<String, Object> nameResponseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/terms/name/{name}", name))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // name - then
        assertThat(objectMapper.convertValue(nameResponseMap.get(DATA),
                new TypeReference<TermResponse>() {
                })
        ).isEqualTo(null);
    }

    @DisplayName("약관 삽입")
    @Test
    void insertTermTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        when(termApplicationService.insert(termsOfUseInsertRequest)).thenReturn(termsOfUseResponse);

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/terms")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(termsOfUseInsertRequest)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<TermResponse>() {
                })
        ).isEqualTo(termsOfUseResponse);
    }

    @DisplayName("약관 갱신")
    @Test
    void updateTermTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        when(termApplicationService.update(termsOfUseUpdateRequest)).thenReturn(termsOfUseResponse);

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(put("/api/v1/terms")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(termsOfUseUpdateRequest)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<TermResponse>() {
                })
        ).isEqualTo(termsOfUseResponse);
    }

    @DisplayName("UUID로 약관 제거")
    @Test
    void removeTermByUuidTest() throws Exception {
        // given
        UUID uuid = termsOfUseWithUuid.getUuid();

        doNothing().when(termApplicationService).removeByUuid(uuid);

        // when & then
        mockMvc.perform(delete("/api/v1/terms/{uuid}", uuid))
                .andExpect(status().isOk());
    }
}