package kr.modusplant.domains.communication.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.common.context.DomainsControllerOnlyContext;
import kr.modusplant.domains.communication.app.http.response.CommCategoryResponse;
import kr.modusplant.domains.communication.app.service.CommPrimaryCategoryApplicationService;
import kr.modusplant.domains.communication.common.util.app.http.request.CommCategoryRequestTestUtils;
import kr.modusplant.domains.communication.common.util.app.http.response.CommCategoryResponseTestUtils;
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
class CommPrimaryCategoryControllerTest implements CommCategoryRequestTestUtils, CommCategoryResponseTestUtils {

    private final MockMvc mockMvc;

    private final CommPrimaryCategoryApplicationService commCategoryApplicationService;

    @Autowired
    CommPrimaryCategoryControllerTest(MockMvc mockMvc, CommPrimaryCategoryApplicationService commCategoryApplicationService) {
        this.mockMvc = mockMvc;
        this.commCategoryApplicationService = commCategoryApplicationService;
    }

    @DisplayName("모든 컨텐츠 1차 항목 얻기")
    @Test
    void getAllCommCategoriesTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        List<CommCategoryResponse> testCommCategoryResponseList = List.of(TEST_COMM_PRIMARY_CATEGORY_RESPONSE);

        when(commCategoryApplicationService.getAll()).thenReturn(testCommCategoryResponseList);

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/communication/categories/primary"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<List<CommCategoryResponse>>() {
                })
        ).isEqualTo(testCommCategoryResponseList);
    }

    @DisplayName("UUID로 컨텐츠 1차 항목 얻기")
    @Test
    void getCommCategoryByUuidTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        UUID uuid = TEST_COMM_PRIMARY_CATEGORY_WITH_UUID.getUuid();

        when(commCategoryApplicationService.getByUuid(uuid)).thenReturn(Optional.of(TEST_COMM_PRIMARY_CATEGORY_RESPONSE));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/communication/categories/primary/{uuid}", uuid))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<CommCategoryResponse>() {
                })
        ).isEqualTo(TEST_COMM_PRIMARY_CATEGORY_RESPONSE);
    }

    @DisplayName("순서로 컨텐츠 1차 항목 얻기")
    @Test
    void getCommCategoryByOrderTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        Integer order = TEST_COMM_PRIMARY_CATEGORY.getOrder();

        when(commCategoryApplicationService.getByOrder(order)).thenReturn(Optional.of(TEST_COMM_PRIMARY_CATEGORY_RESPONSE));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/communication/categories/primary/order/{order}", order))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<CommCategoryResponse>() {
                })
        ).isEqualTo(TEST_COMM_PRIMARY_CATEGORY_RESPONSE);
    }

    @DisplayName("1차 항목으로 컨텐츠 1차 항목 얻기")
    @Test
    void getCommCategoryByNameTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        String category = TEST_COMM_PRIMARY_CATEGORY.getCategory();

        when(commCategoryApplicationService.getByCategory(category)).thenReturn(Optional.of(TEST_COMM_PRIMARY_CATEGORY_RESPONSE));

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/communication/categories/primary/category/{category}", category))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<CommCategoryResponse>() {
                })
        ).isEqualTo(TEST_COMM_PRIMARY_CATEGORY_RESPONSE);
    }

    @DisplayName("빈 컨텐츠 1차 항목 얻기")
    @Test
    void getEmptyCommCategoryTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        Integer order = TEST_COMM_PRIMARY_CATEGORY.getOrder();
        String category = TEST_COMM_PRIMARY_CATEGORY.getCategory();

        when(commCategoryApplicationService.getByOrder(order)).thenReturn(Optional.empty());
        when(commCategoryApplicationService.getByCategory(category)).thenReturn(Optional.empty());

        // order - when
        Map<String, Object> uuidResponseMap = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/communication/categories/primary/order/{order}", order))
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
                mockMvc.perform(get("/api/v1/communication/categories/primary/category/{category}", category))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // category - then
        assertThat(objectMapper.convertValue(nameResponseMap.get(DATA),
                new TypeReference<CommCategoryResponse>() {
                })
        ).isEqualTo(null);
    }

    @DisplayName("컨텐츠 1차 항목 삽입")
    @Test
    void insertCommCategoryTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        when(commCategoryApplicationService.insert(TEST_COMM_PRIMARY_CATEGORY_INSERT_REQUEST)).thenReturn(TEST_COMM_PRIMARY_CATEGORY_RESPONSE);

        // when
        Map<String, Object> responseMap = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/communication/categories/primary")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(TEST_COMM_PRIMARY_CATEGORY_INSERT_REQUEST)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<CommCategoryResponse>() {
                })
        ).isEqualTo(TEST_COMM_PRIMARY_CATEGORY_RESPONSE);
    }

    @DisplayName("순서로 컨텐츠 1차 항목 제거")
    @Test
    void removeCommCategoryByOrderTest() throws Exception {
        // given
        UUID order = TEST_COMM_PRIMARY_CATEGORY_WITH_UUID.getUuid();

        doNothing().when(commCategoryApplicationService).removeByUuid(order);

        // when & then
        mockMvc.perform(delete("/api/v1/communication/categories/primary/{uuid}", order))
                .andExpect(status().isOk());
    }
}