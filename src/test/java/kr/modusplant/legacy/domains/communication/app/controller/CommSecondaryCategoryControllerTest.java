package kr.modusplant.legacy.domains.communication.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.legacy.domains.common.context.DomainsControllerOnlyContext;
import kr.modusplant.legacy.domains.communication.app.http.response.CommCategoryResponse;
import kr.modusplant.legacy.domains.communication.app.service.CommSecondaryCategoryApplicationService;
import kr.modusplant.legacy.domains.communication.common.util.app.http.request.CommCategoryRequestTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.app.http.response.CommCategoryResponseTestUtils;
import kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.legacy.domains.common.constant.FileSystem.DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DomainsControllerOnlyContext
class CommSecondaryCategoryControllerTest implements CommCategoryRequestTestUtils, CommCategoryResponseTestUtils {

    private final MockMvc mockMvc;

    private final CommSecondaryCategoryApplicationService commCategoryApplicationService;

    @Autowired
    CommSecondaryCategoryControllerTest(MockMvc mockMvc, CommSecondaryCategoryApplicationService commCategoryApplicationService) {
        this.mockMvc = mockMvc;
        this.commCategoryApplicationService = commCategoryApplicationService;
    }

    @DisplayName("모든 컨텐츠 2차 항목 얻기")
    @Test
    void getAllCommCategoriesTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        List<CommCategoryResponse> testCommCategoryResponseList = List.of(TEST_COMM_SECONDARY_CATEGORY_RESPONSE);

        when(commCategoryApplicationService.getAll()).thenReturn(testCommCategoryResponseList);

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/communication/categories/secondary"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<List<CommCategoryResponse>>() {
                })
        ).isEqualTo(testCommCategoryResponseList);
    }

    @DisplayName("UUID로 컨텐츠 2차 항목 얻기")
    @Test
    void getCommCategoryByUuidTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        UUID uuid = CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_UUID;

        when(commCategoryApplicationService.getByUuid(uuid)).thenReturn(Optional.of(TEST_COMM_SECONDARY_CATEGORY_RESPONSE));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/communication/categories/secondary/{uuid}", uuid))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<CommCategoryResponse>() {
                })
        ).isEqualTo(TEST_COMM_SECONDARY_CATEGORY_RESPONSE);
    }

    @DisplayName("순서로 컨텐츠 2차 항목 얻기")
    @Test
    void getCommCategoryByOrderTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        Integer order = CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_ORDER;

        when(commCategoryApplicationService.getByOrder(order)).thenReturn(Optional.of(TEST_COMM_SECONDARY_CATEGORY_RESPONSE));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/communication/categories/secondary/order/{order}", order))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<CommCategoryResponse>() {
                })
        ).isEqualTo(TEST_COMM_SECONDARY_CATEGORY_RESPONSE);
    }

    @DisplayName("2차 항목으로 컨텐츠 2차 항목 얻기")
    @Test
    void getCommCategoryByNameTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        String category = CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_CATEGORY;

        when(commCategoryApplicationService.getByCategory(category)).thenReturn(Optional.of(TEST_COMM_SECONDARY_CATEGORY_RESPONSE));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/communication/categories/secondary/category/{category}", category))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<CommCategoryResponse>() {
                })
        ).isEqualTo(TEST_COMM_SECONDARY_CATEGORY_RESPONSE);
    }

    @DisplayName("빈 컨텐츠 2차 항목 얻기")
    @Test
    void getEmptyCommCategoryTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        Integer order = CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_ORDER;
        String category = CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_CATEGORY;

        when(commCategoryApplicationService.getByOrder(order)).thenReturn(Optional.empty());
        when(commCategoryApplicationService.getByCategory(category)).thenReturn(Optional.empty());

        // order - when
        Map<String, Object> uuidResponseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/communication/categories/secondary/order/{order}", order))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // order - then
        assertThat(objectMapper.convertValue(uuidResponseMap.get(DATA),
                new TypeReference<CommCategoryResponse>() {
                })
        ).isEqualTo(null);

        // category - when
        Map<String, Object> nameResponseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/communication/categories/secondary/category/{category}", category))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // category - then
        assertThat(objectMapper.convertValue(nameResponseMap.get(DATA),
                new TypeReference<CommCategoryResponse>() {
                })
        ).isEqualTo(null);
    }

    @DisplayName("컨텐츠 2차 항목 삽입")
    @Test
    void insertCommCategoryTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        when(commCategoryApplicationService.insert(TEST_COMM_SECONDARY_CATEGORY_INSERT_REQUEST)).thenReturn(TEST_COMM_SECONDARY_CATEGORY_RESPONSE);

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/communication/categories/secondary")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(TEST_COMM_SECONDARY_CATEGORY_INSERT_REQUEST)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<CommCategoryResponse>() {
                })
        ).isEqualTo(TEST_COMM_SECONDARY_CATEGORY_RESPONSE);
    }

    @DisplayName("순서로 컨텐츠 2차 항목 제거")
    @Test
    void removeCommCategoryByOrderTest() throws Exception {
        // given
        UUID order = CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_UUID;

        doNothing().when(commCategoryApplicationService).removeByUuid(order);

        // when & then
        mockMvc.perform(delete("/api/v1/communication/categories/secondary/{uuid}", order))
                .andExpect(status().isOk());
    }
}