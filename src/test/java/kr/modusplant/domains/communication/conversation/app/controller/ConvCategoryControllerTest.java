package kr.modusplant.domains.communication.conversation.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.common.context.DomainsControllerOnlyContext;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvCategoryResponse;
import kr.modusplant.domains.communication.conversation.app.service.ConvCategoryApplicationService;
import kr.modusplant.domains.communication.conversation.common.util.app.http.request.ConvCategoryRequestTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.app.http.response.ConvCategoryResponseTestUtils;
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
class ConvCategoryControllerTest implements ConvCategoryRequestTestUtils, ConvCategoryResponseTestUtils {

    private final MockMvc mockMvc;

    @Autowired
    private final ConvCategoryApplicationService convCategoryApplicationService;

    @Autowired
    ConvCategoryControllerTest(MockMvc mockMvc, ConvCategoryApplicationService convCategoryApplicationService) {
        this.mockMvc = mockMvc;
        this.convCategoryApplicationService = convCategoryApplicationService;
    }

    @DisplayName("모든 대화 항목 얻기")
    @Test
    void getAllConvCategoriesTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        List<ConvCategoryResponse> testConvCategoryResponseList = List.of(testConvCategoryResponse);

        when(convCategoryApplicationService.getAll()).thenReturn(testConvCategoryResponseList);

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/conversation/categories"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<List<ConvCategoryResponse>>() {
                })
        ).isEqualTo(testConvCategoryResponseList);
    }

    @DisplayName("UUID로 대화 항목 얻기")
    @Test
    void getConvCategoryByUuidTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        UUID uuid = testConvCategoryWithUuid.getUuid();

        when(convCategoryApplicationService.getByUuid(uuid)).thenReturn(Optional.of(testConvCategoryResponse));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/conversation/categories/{uuid}", uuid))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<ConvCategoryResponse>() {
                })
        ).isEqualTo(testConvCategoryResponse);
    }

    @DisplayName("순서로 대화 항목 얻기")
    @Test
    void getConvCategoryByOrderTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        Integer order = testConvCategory.getOrder();

        when(convCategoryApplicationService.getByOrder(order)).thenReturn(Optional.of(testConvCategoryResponse));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/conversation/categories/order/{order}", order))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<ConvCategoryResponse>() {
                })
        ).isEqualTo(testConvCategoryResponse);
    }

    @DisplayName("항목으로 대화 항목 얻기")
    @Test
    void getConvCategoryByNameTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        String category = testConvCategory.getCategory();

        when(convCategoryApplicationService.getByCategory(category)).thenReturn(Optional.of(testConvCategoryResponse));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/conversation/categories/category/{category}", category))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<ConvCategoryResponse>() {
                })
        ).isEqualTo(testConvCategoryResponse);
    }

    @DisplayName("빈 대화 항목 얻기")
    @Test
    void getEmptyConvCategoryTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        Integer order = testConvCategory.getOrder();
        String category = testConvCategory.getCategory();

        when(convCategoryApplicationService.getByOrder(order)).thenReturn(Optional.empty());
        when(convCategoryApplicationService.getByCategory(category)).thenReturn(Optional.empty());

        // order - when
        Map<String, Object> uuidResponseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/conversation/categories/order/{order}", order))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // order - then
        assertThat(objectMapper.convertValue(uuidResponseMap.get(DATA),
                new TypeReference<ConvCategoryResponse>() {
                })
        ).isEqualTo(null);

        // category - when
        Map<String, Object> nameResponseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/conversation/categories/category/{category}", category))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // category - then
        assertThat(objectMapper.convertValue(nameResponseMap.get(DATA),
                new TypeReference<ConvCategoryResponse>() {
                })
        ).isEqualTo(null);
    }

    @DisplayName("대화 항목 삽입")
    @Test
    void insertConvCategoryTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        when(convCategoryApplicationService.insert(testConvCategoryInsertRequest)).thenReturn(testConvCategoryResponse);

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/conversation/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testConvCategoryInsertRequest)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<ConvCategoryResponse>() {
                })
        ).isEqualTo(testConvCategoryResponse);
    }

    @DisplayName("순서로 대화 항목 제거")
    @Test
    void removeConvCategoryByOrderTest() throws Exception {
        // given
        UUID order = testConvCategoryWithUuid.getUuid();

        doNothing().when(convCategoryApplicationService).removeByUuid(order);

        // when & then
        mockMvc.perform(delete("/api/v1/conversation/categories/{uuid}", order))
                .andExpect(status().isOk());
    }
}