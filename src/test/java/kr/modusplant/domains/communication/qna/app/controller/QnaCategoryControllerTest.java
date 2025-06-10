package kr.modusplant.domains.communication.qna.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.common.context.DomainsControllerOnlyContext;
import kr.modusplant.domains.communication.qna.app.http.response.QnaCategoryResponse;
import kr.modusplant.domains.communication.qna.app.service.QnaCategoryApplicationService;
import kr.modusplant.domains.communication.qna.common.util.app.http.request.QnaCategoryRequestTestUtils;
import kr.modusplant.domains.communication.qna.common.util.app.http.response.QnaCategoryResponseTestUtils;
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
class QnaCategoryControllerTest implements QnaCategoryRequestTestUtils, QnaCategoryResponseTestUtils {

    private final MockMvc mockMvc;

    @Autowired
    private final QnaCategoryApplicationService qnaCategoryApplicationService;

    @Autowired
    QnaCategoryControllerTest(MockMvc mockMvc, QnaCategoryApplicationService qnaCategoryApplicationService) {
        this.mockMvc = mockMvc;
        this.qnaCategoryApplicationService = qnaCategoryApplicationService;
    }

    @DisplayName("모든 Q&A 항목 얻기")
    @Test
    void getAllQnaCategoriesTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        List<QnaCategoryResponse> testQnaCategoryResponseList = List.of(testQnaCategoryResponse);

        when(qnaCategoryApplicationService.getAll()).thenReturn(testQnaCategoryResponseList);

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/qna/categories"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<List<QnaCategoryResponse>>() {
                })
        ).isEqualTo(testQnaCategoryResponseList);
    }

    @DisplayName("UUID로 Q&A 항목 얻기")
    @Test
    void getQnaCategoryByUuidTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        UUID uuid = testQnaCategoryWithUuid.getUuid();

        when(qnaCategoryApplicationService.getByUuid(uuid)).thenReturn(Optional.of(testQnaCategoryResponse));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/qna/categories/{uuid}", uuid))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<QnaCategoryResponse>() {
                })
        ).isEqualTo(testQnaCategoryResponse);
    }

    @DisplayName("순서로 Q&A 항목 얻기")
    @Test
    void getQnaCategoryByOrderTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        Integer order = testQnaCategory.getOrder();

        when(qnaCategoryApplicationService.getByOrder(order)).thenReturn(Optional.of(testQnaCategoryResponse));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/qna/categories/order/{order}", order))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<QnaCategoryResponse>() {
                })
        ).isEqualTo(testQnaCategoryResponse);
    }

    @DisplayName("항목으로 Q&A 항목 얻기")
    @Test
    void getQnaCategoryByNameTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        String category = testQnaCategory.getCategory();

        when(qnaCategoryApplicationService.getByCategory(category)).thenReturn(Optional.of(testQnaCategoryResponse));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/qna/categories/category/{category}", category))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<QnaCategoryResponse>() {
                })
        ).isEqualTo(testQnaCategoryResponse);
    }

    @DisplayName("빈 Q&A 항목 얻기")
    @Test
    void getEmptyQnaCategoryTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        Integer order = testQnaCategory.getOrder();
        String category = testQnaCategory.getCategory();

        when(qnaCategoryApplicationService.getByOrder(order)).thenReturn(Optional.empty());
        when(qnaCategoryApplicationService.getByCategory(category)).thenReturn(Optional.empty());

        // order - when
        Map<String, Object> uuidResponseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/qna/categories/order/{order}", order))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // order - then
        assertThat(objectMapper.convertValue(uuidResponseMap.get(DATA),
                new TypeReference<QnaCategoryResponse>() {
                })
        ).isEqualTo(null);

        // category - when
        Map<String, Object> nameResponseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/qna/categories/category/{category}", category))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // category - then
        assertThat(objectMapper.convertValue(nameResponseMap.get(DATA),
                new TypeReference<QnaCategoryResponse>() {
                })
        ).isEqualTo(null);
    }

    @DisplayName("Q&A 항목 삽입")
    @Test
    void insertQnaCategoryTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        when(qnaCategoryApplicationService.insert(testQnaCategoryInsertRequest)).thenReturn(testQnaCategoryResponse);

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/qna/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testQnaCategoryInsertRequest)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<QnaCategoryResponse>() {
                })
        ).isEqualTo(testQnaCategoryResponse);
    }

    @DisplayName("순서로 Q&A 항목 제거")
    @Test
    void removeQnaCategoryByOrderTest() throws Exception {
        // given
        UUID order = testQnaCategoryWithUuid.getUuid();

        doNothing().when(qnaCategoryApplicationService).removeByUuid(order);

        // when & then
        mockMvc.perform(delete("/api/v1/qna/categories/{uuid}", order))
                .andExpect(status().isOk());
    }
}