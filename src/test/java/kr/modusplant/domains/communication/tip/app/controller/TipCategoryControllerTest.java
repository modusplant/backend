package kr.modusplant.domains.communication.tip.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.common.context.DomainsControllerOnlyContext;
import kr.modusplant.domains.communication.tip.app.http.response.TipCategoryResponse;
import kr.modusplant.domains.communication.tip.app.service.TipCategoryApplicationService;
import kr.modusplant.domains.communication.tip.common.util.app.http.request.TipCategoryRequestTestUtils;
import kr.modusplant.domains.communication.tip.common.util.app.http.response.TipCategoryResponseTestUtils;
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
class TipCategoryControllerTest implements TipCategoryRequestTestUtils, TipCategoryResponseTestUtils {

    private final MockMvc mockMvc;

    @Autowired
    private final TipCategoryApplicationService tipCategoryApplicationService;

    @Autowired
    TipCategoryControllerTest(MockMvc mockMvc, TipCategoryApplicationService tipCategoryApplicationService) {
        this.mockMvc = mockMvc;
        this.tipCategoryApplicationService = tipCategoryApplicationService;
    }

    @DisplayName("모든 팁 항목 얻기")
    @Test
    void getAllTipCategoriesTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        List<TipCategoryResponse> testTipCategoryResponseList = List.of(testTipCategoryResponse);

        when(tipCategoryApplicationService.getAll()).thenReturn(testTipCategoryResponseList);

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/tip/categories"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<List<TipCategoryResponse>>() {
                })
        ).isEqualTo(testTipCategoryResponseList);
    }

    @DisplayName("UUID로 팁 항목 얻기")
    @Test
    void getTipCategoryByUuidTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        UUID uuid = testTipCategoryWithUuid.getUuid();

        when(tipCategoryApplicationService.getByUuid(uuid)).thenReturn(Optional.of(testTipCategoryResponse));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/tip/categories/{uuid}", uuid))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<TipCategoryResponse>() {
                })
        ).isEqualTo(testTipCategoryResponse);
    }

    @DisplayName("순서로 팁 항목 얻기")
    @Test
    void getTipCategoryByOrderTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        Integer order = testTipCategory.getOrder();

        when(tipCategoryApplicationService.getByOrder(order)).thenReturn(Optional.of(testTipCategoryResponse));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/tip/categories/order/{order}", order))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<TipCategoryResponse>() {
                })
        ).isEqualTo(testTipCategoryResponse);
    }

    @DisplayName("항목으로 팁 항목 얻기")
    @Test
    void getTipCategoryByNameTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        String category = testTipCategory.getCategory();

        when(tipCategoryApplicationService.getByCategory(category)).thenReturn(Optional.of(testTipCategoryResponse));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/tip/categories/category/{category}", category))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<TipCategoryResponse>() {
                })
        ).isEqualTo(testTipCategoryResponse);
    }

    @DisplayName("빈 팁 항목 얻기")
    @Test
    void getEmptyTipCategoryTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        Integer order = testTipCategory.getOrder();
        String category = testTipCategory.getCategory();

        when(tipCategoryApplicationService.getByOrder(order)).thenReturn(Optional.empty());
        when(tipCategoryApplicationService.getByCategory(category)).thenReturn(Optional.empty());

        // order - when
        Map<String, Object> uuidResponseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/tip/categories/order/{order}", order))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // order - then
        assertThat(objectMapper.convertValue(uuidResponseMap.get(DATA),
                new TypeReference<TipCategoryResponse>() {
                })
        ).isEqualTo(null);

        // category - when
        Map<String, Object> nameResponseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/tip/categories/category/{category}", category))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // category - then
        assertThat(objectMapper.convertValue(nameResponseMap.get(DATA),
                new TypeReference<TipCategoryResponse>() {
                })
        ).isEqualTo(null);
    }

    @DisplayName("팁 항목 삽입")
    @Test
    void insertTipCategoryTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        when(tipCategoryApplicationService.insert(testTipCategoryInsertRequest)).thenReturn(testTipCategoryResponse);

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/tip/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testTipCategoryInsertRequest)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<TipCategoryResponse>() {
                })
        ).isEqualTo(testTipCategoryResponse);
    }

    @DisplayName("순서로 팁 항목 제거")
    @Test
    void removeTipCategoryByOrderTest() throws Exception {
        // given
        UUID order = testTipCategoryWithUuid.getUuid();

        doNothing().when(tipCategoryApplicationService).removeByUuid(order);

        // when & then
        mockMvc.perform(delete("/api/v1/tip/categories/{uuid}", order))
                .andExpect(status().isOk());
    }
}