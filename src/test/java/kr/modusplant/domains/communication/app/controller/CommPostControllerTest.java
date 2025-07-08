package kr.modusplant.domains.communication.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.modusplant.domains.common.context.DomainsControllerOnlyContext;
import kr.modusplant.domains.communication.app.http.request.CommPostInsertRequest;
import kr.modusplant.domains.communication.app.http.request.CommPostUpdateRequest;
import kr.modusplant.domains.communication.app.http.response.CommPostPageResponse;
import kr.modusplant.domains.communication.app.http.response.CommPostResponse;
import kr.modusplant.domains.communication.app.service.CommPostApplicationService;
import kr.modusplant.domains.communication.common.util.app.http.request.CommPostRequestTestUtils;
import kr.modusplant.domains.communication.common.util.app.http.response.CommPostResponseTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DomainsControllerOnlyContext
class CommPostControllerTest implements CommPostRequestTestUtils, CommPostResponseTestUtils {

    private final MockMvc mockMvc;

    private final CommPostApplicationService commPostApplicationService;

    @Autowired
    CommPostControllerTest(MockMvc mockMvc, CommPostApplicationService commPostApplicationService) {
        this.mockMvc = mockMvc;
        this.commPostApplicationService = commPostApplicationService;
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    private final String BASE_URL = "/api/v1/communication/posts";
    private final UUID TEST_POST_MEMBER_UUID = UUID.randomUUID();
    private final UUID TEST_PRIMARY_CATEGORY_UUID = UUID.randomUUID();
    private final UUID TEST_SECONDARY_CATEGORY_UUID = UUID.randomUUID();
    private final String TEST_POST_ULID = "test-ulid";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 형식으로 출력
    }

    @Test
    @DisplayName("전체 게시글 목록 조회하기")
    void getAllCommPostsTest() throws Exception {
        // given
        Page<CommPostResponse> commPostResponse = new PageImpl<>(List.of(TEST_COMM_POST_RESPONSE));
        CommPostPageResponse<CommPostResponse> commCommPostPageResponse = CommPostPageResponse.from(commPostResponse);
        when(commPostApplicationService.getAll(any(Pageable.class))).thenReturn(commPostResponse);

        // when
        Map<String,Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get(BASE_URL)
                                .param("page", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<CommPostPageResponse<CommPostResponse>>() {
                })
        ).isEqualTo(commCommPostPageResponse);
    }

    @Test
    @DisplayName("회원별 게시글 목록 조회하기")
    void getCommPostsByMemberTest() throws Exception {
        // given
        Page<CommPostResponse> commPostResponse = new PageImpl<>(List.of(TEST_COMM_POST_RESPONSE));
        CommPostPageResponse<CommPostResponse> commCommPostPageResponse = CommPostPageResponse.from(commPostResponse);
        when(commPostApplicationService.getByMemberUuid(any(UUID.class), any(Pageable.class))).thenReturn(commPostResponse);

        // when
        Map<String,Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get(BASE_URL + "/member/" + TEST_POST_MEMBER_UUID)
                                .param("page", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<CommPostPageResponse<CommPostResponse>>() {
                })
        ).isEqualTo(commCommPostPageResponse);
    }

    @Test
    @DisplayName("1차 항목별 게시글 목록 조회하기")
    void getCommPostsPrimaryCategoryTest() throws Exception {
        // given
        Page<CommPostResponse> commPostResponse = new PageImpl<>(List.of(TEST_COMM_POST_RESPONSE));
        CommPostPageResponse<CommPostResponse> commCommPostPageResponse = CommPostPageResponse.from(commPostResponse);
        when(commPostApplicationService.getByPrimaryCategoryUuid(any(UUID.class), any(Pageable.class))).thenReturn(commPostResponse);

        // when
        Map<String,Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get(BASE_URL + "/category/primary/" + TEST_PRIMARY_CATEGORY_UUID)
                                .param("page", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<CommPostPageResponse<CommPostResponse>>() {
                })
        ).isEqualTo(commCommPostPageResponse);
    }

    @Test
    @DisplayName("2차 항목별 게시글 목록 조회하기")
    void getCommPostsSecondaryCategoryTest() throws Exception {
        // given
        Page<CommPostResponse> commPostResponse = new PageImpl<>(List.of(TEST_COMM_POST_RESPONSE));
        CommPostPageResponse<CommPostResponse> commCommPostPageResponse = CommPostPageResponse.from(commPostResponse);
        when(commPostApplicationService.getBySecondaryCategoryUuid(any(UUID.class), any(Pageable.class))).thenReturn(commPostResponse);

        // when
        Map<String,Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get(BASE_URL + "/category/secondary/" + TEST_SECONDARY_CATEGORY_UUID)
                                .param("page", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<CommPostPageResponse<CommPostResponse>>() {
                })
        ).isEqualTo(commCommPostPageResponse);
    }

    @Test
    @DisplayName("검색어로 게시글 목록 조회하기")
    void searchCommPostsTest() throws Exception {
        // given
        String keyword = "test";
        Page<CommPostResponse> commPostResponse = new PageImpl<>(List.of(TEST_COMM_POST_RESPONSE));
        CommPostPageResponse<CommPostResponse> commCommPostPageResponse = CommPostPageResponse.from(commPostResponse);
        when(commPostApplicationService.searchByKeyword(anyString(), any(Pageable.class))).thenReturn(commPostResponse);

        // when
        Map<String,Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get(BASE_URL + "/search")
                                .param("keyword", keyword)
                                .param("page", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<CommPostPageResponse<CommPostResponse>>() {
                })
        ).isEqualTo(commCommPostPageResponse);
    }

    @Test
    @DisplayName("ULID로 특정 게시글 조회하기")
    void getCommPostByUlidTest() throws Exception {
        // given
        when(commPostApplicationService.getByUlid(anyString())).thenReturn(Optional.of(TEST_COMM_POST_RESPONSE));

        // when
        Map<String,Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get(BASE_URL + "/" + TEST_POST_ULID)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<CommPostResponse>() {
                })
        ).isEqualTo(TEST_COMM_POST_RESPONSE);
    }

    @Test
    @DisplayName("ULID로 특정 게시글 조회하기 (empty)")
    void getEmptyCommPostByUlidTest() throws Exception {
        // given
        when(commPostApplicationService.getByUlid(anyString())).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get(BASE_URL + "/" + TEST_POST_ULID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 생성하기")
    void insertCommPostTest() throws Exception {
        // given
        String title = "Test Post Title";
        MockMultipartFile mockTextFile = (MockMultipartFile) textFile0;
        MockMultipartFile mockImageFile = (MockMultipartFile) imageFile;
        MockMultipartFile mockOrderInfoPart = new MockMultipartFile("orderInfo", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(textImageFilesOrder));

        doNothing().when(commPostApplicationService).insert(any(CommPostInsertRequest.class), any(UUID.class));

        // when & then
        mockMvc.perform(multipart(BASE_URL)
                        .file(mockTextFile)
                        .file(mockImageFile)
                        .file(mockOrderInfoPart)
                        .param("primaryCategoryUuid", TEST_PRIMARY_CATEGORY_UUID.toString())
                        .param("secondaryCategoryUuid", TEST_SECONDARY_CATEGORY_UUID.toString())
                        .param("title", title)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(commPostApplicationService, times(1))
                .insert(argThat(req ->
                        req.primaryCategoryUuid().equals(TEST_PRIMARY_CATEGORY_UUID) &&
                                req.secondaryCategoryUuid().equals(TEST_SECONDARY_CATEGORY_UUID) &&
                                req.title().equals(title) &&
                                req.content().size() == 2 &&
                                req.orderInfo().size() == 2
                ), any(UUID.class));
    }

    @Test
    @DisplayName("게시글 수정하기")
    void updateCommPostTest() throws Exception {
        // given
        String title = "Test Post Title";
        MockMultipartFile mockTextFile = (MockMultipartFile) textFile0;
        MockMultipartFile mockImageFile = (MockMultipartFile) imageFile;
        MockMultipartFile mockOrderInfoPart = new MockMultipartFile("orderInfo", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(textImageFilesOrder));

        doNothing().when(commPostApplicationService).update(any(CommPostUpdateRequest.class), any(UUID.class));

        // when & then
        mockMvc.perform(multipart(BASE_URL + "/" + TEST_POST_ULID)
                        .file(mockTextFile)
                        .file(mockImageFile)
                        .file(mockOrderInfoPart)
                        .param("primaryCategoryUuid", TEST_PRIMARY_CATEGORY_UUID.toString())
                        .param("secondaryCategoryUuid", TEST_SECONDARY_CATEGORY_UUID.toString())
                        .param("title", title)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(commPostApplicationService, times(1))
                .update(argThat(req ->
                        req.ulid().equals(TEST_POST_ULID) &&
                                req.primaryCategoryUuid().equals(TEST_PRIMARY_CATEGORY_UUID) &&
                                req.secondaryCategoryUuid().equals(TEST_SECONDARY_CATEGORY_UUID) &&
                                req.title().equals(title) &&
                                req.content().size() == 2 &&
                                req.orderInfo().size() == 2
                ), any(UUID.class));
    }

    @Test
    @DisplayName("ULID로 특정 게시글 삭제하기")
    void removeCommPostByUlidTest() throws Exception {
        // given
        doNothing().when(commPostApplicationService).removeByUlid(anyString(), any(UUID.class));

        // when & then
        mockMvc.perform(delete(BASE_URL + "/" + TEST_POST_ULID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ULID로 특정 게시글의 조회수 구하기")
    void countViewCountTest() throws Exception {
        // given
        when(commPostApplicationService.readViewCount(anyString())).thenReturn(50L);

        // when & then
        mockMvc.perform(get(BASE_URL + "/" + TEST_POST_ULID + "/views")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(50L));
    }

    @Test
    @DisplayName("ULID로 특정 게시글의 조회수 증가시키기")
    void increaseViewCountTest() throws Exception {
        // given
        when(commPostApplicationService.increaseViewCount(anyString(), any(UUID.class))).thenReturn(51L);

        // when & then
        mockMvc.perform(patch(BASE_URL + "/" + TEST_POST_ULID + "/views")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(51L));
    }
}