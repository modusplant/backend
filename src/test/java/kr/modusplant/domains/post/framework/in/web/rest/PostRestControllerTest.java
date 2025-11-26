package kr.modusplant.domains.post.framework.in.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.modusplant.domains.post.adapter.controller.PostController;
import kr.modusplant.domains.post.common.util.usecase.request.PostRequestTestUtils;
import kr.modusplant.domains.post.common.util.usecase.response.PostResponseTestUtils;
import kr.modusplant.domains.post.usecase.request.PostCategoryRequest;
import kr.modusplant.domains.post.usecase.request.PostInsertRequest;
import kr.modusplant.domains.post.usecase.request.PostUpdateRequest;
import kr.modusplant.domains.post.usecase.response.CursorPageResponse;
import kr.modusplant.domains.post.usecase.response.PostDetailResponse;
import kr.modusplant.domains.post.usecase.response.PostSummaryResponse;
import kr.modusplant.infrastructure.advice.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.post.common.constant.PostStringConstant.TEST_POST_TITLE;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID2;
import static kr.modusplant.domains.post.common.constant.PostUuidConstant.TEST_POST_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostRestControllerTest implements PostRequestTestUtils, PostResponseTestUtils {

    private MockMvc mockMvc;

    @Mock
    private PostController postController;

    @InjectMocks
    private PostRestController postRestController;

    private final String BASE_URL = "/api/v1/communication/posts";
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postRestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        ReflectionTestUtils.setField(postRestController, "currentMemberUuid", TEST_POST_UUID);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 형식으로 출력
    }

    @Test
    @DisplayName("전체 게시글 목록 조회 - 첫 페이지")
    void testGetAllPosts_givenSizeOnly_willReturnFirstPage() throws Exception {
        // given
        String nextUlid = TEST_POST_ULID2;
        CursorPageResponse<PostSummaryResponse> expectedResponse = CursorPageResponse.of(
                List.of(TEST_POST_SUMMARY_RESPONSE),
                nextUlid,
                true
        );

        given(postController.getAll(any(PostCategoryRequest.class), any(UUID.class), isNull(), eq(10)))
                .willReturn(expectedResponse);

        // when & then
        mockMvc.perform(get(BASE_URL)
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.posts").isArray())
                .andExpect(jsonPath("$.data.nextPostId").value(nextUlid))
                .andExpect(jsonPath("$.data.hasNext").value(true));

        verify(postController).getAll(
                argThat(req -> req.primaryCategoryUuid() == null && req.secondaryCategoryUuids() == null),
                any(UUID.class),
                isNull(),
                eq(10)
        );
    }

    @Test
    @DisplayName("전체 게시글 목록 조회 - 커서 기반 다음 페이지")
    void testGetAllPosts_givenLastUlidAndSize_willReturnNextPage() throws Exception {
        // given
        String lastUlid = TEST_POST_ULID2;
        String nextUlid = TEST_POST_ULID;
        CursorPageResponse<PostSummaryResponse> expectedResponse = CursorPageResponse.of(
                List.of(TEST_POST_SUMMARY_RESPONSE),
                nextUlid,
                true
        );

        given(postController.getAll(any(PostCategoryRequest.class), any(UUID.class), eq(lastUlid), eq(10)))
                .willReturn(expectedResponse);

        // when & then
        mockMvc.perform(get(BASE_URL)
                        .param("lastPostId", lastUlid)
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.posts").isArray())
                .andExpect(jsonPath("$.data.nextPostId").value(nextUlid))
                .andExpect(jsonPath("$.data.hasNext").value(true));

        verify(postController).getAll(
                argThat(req -> req.primaryCategoryUuid() == null && req.secondaryCategoryUuids() == null),
                any(UUID.class),
                eq(lastUlid),
                eq(10)
        );
    }

    @Test
    @DisplayName("전체 게시글 목록 조회 - 1차 카테고리 필터링")
    void testGetAllPosts_givenPrimaryCategoryUuid_willReturnFilteredPosts() throws Exception {
        // given
        CursorPageResponse<PostSummaryResponse> expectedResponse = CursorPageResponse.of(
                List.of(TEST_POST_SUMMARY_RESPONSE),
                null,
                false
        );

        given(postController.getAll(any(PostCategoryRequest.class), any(UUID.class), isNull(), eq(10)))
                .willReturn(expectedResponse);

        // when & then
        mockMvc.perform(get(BASE_URL)
                        .param("primaryCategoryId", TEST_COMM_PRIMARY_CATEGORY_UUID.toString())
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.posts").isArray())
                .andExpect(jsonPath("$.data.nextPostId").isEmpty())
                .andExpect(jsonPath("$.data.hasNext").value(false));

        verify(postController).getAll(
                argThat(req ->
                        req.primaryCategoryUuid().equals(TEST_COMM_PRIMARY_CATEGORY_UUID) &&
                                req.secondaryCategoryUuids() == null
                ),
                any(UUID.class),
                isNull(),
                eq(10)
        );
    }

    @Test
    @DisplayName("전체 게시글 목록 조회 - 1차 및 2차 카테고리 필터링")
    void testGetAllPosts_givenPrimaryAndSecondaryCategoryUuids_willReturnFilteredPosts() throws Exception {
        // given
        CursorPageResponse<PostSummaryResponse> expectedResponse = CursorPageResponse.of(
                List.of(TEST_POST_SUMMARY_RESPONSE),
                null,
                false
        );

        given(postController.getAll(any(PostCategoryRequest.class), any(UUID.class), isNull(), eq(10)))
                .willReturn(expectedResponse);

        // when & then
        mockMvc.perform(get(BASE_URL)
                        .param("primaryCategoryId", TEST_COMM_PRIMARY_CATEGORY_UUID.toString())
                        .param("secondaryCategoryId", TEST_COMM_SECONDARY_CATEGORY_UUID.toString())
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.posts").isArray());

        verify(postController).getAll(
                argThat(req ->
                        req.primaryCategoryUuid().equals(TEST_COMM_PRIMARY_CATEGORY_UUID) &&
                                req.secondaryCategoryUuids() != null &&
                                req.secondaryCategoryUuids().contains(TEST_COMM_SECONDARY_CATEGORY_UUID)
                ),
                any(UUID.class),
                isNull(),
                eq(10)
        );
    }

    @Test
    @DisplayName("전체 게시글 목록 조회 - 복수 2차 카테고리 필터링")
    void testGetAllPosts_givenMultipleSecondaryCategoryUuids_willReturnFilteredPosts() throws Exception {
        // given
        UUID secondaryCategoryUuid1 = UUID.randomUUID();
        UUID secondaryCategoryUuid2 = UUID.randomUUID();
        CursorPageResponse<PostSummaryResponse> expectedResponse = CursorPageResponse.of(
                List.of(TEST_POST_SUMMARY_RESPONSE),
                null,
                false
        );

        given(postController.getAll(any(PostCategoryRequest.class), any(UUID.class), isNull(), eq(10)))
                .willReturn(expectedResponse);

        // when & then
        mockMvc.perform(get(BASE_URL)
                        .param("primaryCategoryId", TEST_COMM_PRIMARY_CATEGORY_UUID.toString())
                        .param("secondaryCategoryId", secondaryCategoryUuid1.toString())
                        .param("secondaryCategoryId", secondaryCategoryUuid2.toString())
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.posts").isArray());

        verify(postController).getAll(
                argThat(req ->
                        req.primaryCategoryUuid().equals(TEST_COMM_PRIMARY_CATEGORY_UUID) &&
                                req.secondaryCategoryUuids() != null &&
                                req.secondaryCategoryUuids().size() == 2 &&
                                req.secondaryCategoryUuids().contains(secondaryCategoryUuid1) &&
                                req.secondaryCategoryUuids().contains(secondaryCategoryUuid2)
                ),
                any(UUID.class),
                isNull(),
                eq(10)
        );
    }

    @Test
    @DisplayName("전체 게시글 목록 조회 - 1차 카테고리 없이 2차 카테고리만 지정 시 예외 발생")
    void testGetAllPosts_givenSecondaryCategoryOnlyWithoutPrimary_willThrowException() throws Exception {
        // when & then
        mockMvc.perform(get(BASE_URL)
                        .param("secondaryCategoryId", TEST_COMM_SECONDARY_CATEGORY_UUID.toString())
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("empty_category_id"))
                .andExpect(jsonPath("$.message").value("카테고리 id가 비어 있습니다. "));

        verify(postController, never()).getAll(any(), any(), any(), anyInt());
    }

    @Test
    @DisplayName("특정 컨텐츠 게시글 조회하기")
    void testGetPostByUlid_givenUlid_willReturnPost() throws Exception {
        // given
        given(postController.getByUlid(anyString(), any(UUID.class))).willReturn(Optional.of(TEST_POST_DETAIL_RESPONSE));

        // when
        Map<String,Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get(BASE_URL + "/" + TEST_POST_ULID)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get("data"),
                new TypeReference<PostDetailResponse>() {
                })
        ).isEqualTo(TEST_POST_DETAIL_RESPONSE);
    }

    @Test
    @DisplayName("컨텐츠 게시글 추가하기")
    void testInsertPost_givenPostInsertRequest_willInsertPost() throws Exception {
        // given
        MockMultipartFile mockTextFile = (MockMultipartFile) textFile0;
        MockMultipartFile mockImageFile = (MockMultipartFile) imageFile;
        MockMultipartFile mockOrderInfoPart = new MockMultipartFile("orderInfo", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(textImageFilesOrder));

        willDoNothing().given(postController).createPost(any(PostInsertRequest.class),any(UUID.class));

        // when & then
        mockMvc.perform(multipart(BASE_URL)
                .file(mockTextFile)
                .file(mockImageFile)
                .file(mockOrderInfoPart)
                .param("primaryCategoryId", TEST_COMM_PRIMARY_CATEGORY_UUID.toString())
                .param("secondaryCategoryId", TEST_COMM_SECONDARY_CATEGORY_UUID.toString())
                .param("title", TEST_POST_TITLE)
                .param("isPublished", "true")
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isOk());

        verify(postController, times(1))
                .createPost(argThat(req ->
                        req.primaryCategoryUuid().equals(TEST_COMM_PRIMARY_CATEGORY_UUID) &&
                                req.secondaryCategoryUuid().equals(TEST_COMM_SECONDARY_CATEGORY_UUID) &&
                                req.title().equals(TEST_POST_TITLE) &&
                                req.content().size() == 2 &&
                                req.orderInfo().size() == 2 &&
                                req.isPublished().equals(true)
                ), any(UUID.class));
    }

    @Test
    @DisplayName("특정 컨텐츠 게시글 수정하기")
    void testUpdatePost_givenUlidAndPostUpdateRequest_willUpdatePost() throws Exception {
        // given
        MockMultipartFile mockTextFile = (MockMultipartFile) textFile0;
        MockMultipartFile mockImageFile = (MockMultipartFile) imageFile;
        MockMultipartFile mockOrderInfoPart = new MockMultipartFile("orderInfo", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(textImageFilesOrder));

        willDoNothing().given(postController).updatePost(any(PostUpdateRequest.class),any(UUID.class));

        // when & then
        mockMvc.perform(multipart(BASE_URL + "/" + TEST_POST_ULID)
                        .file(mockTextFile)
                        .file(mockImageFile)
                        .file(mockOrderInfoPart)
                        .param("primaryCategoryId", TEST_COMM_PRIMARY_CATEGORY_UUID.toString())
                        .param("secondaryCategoryId", TEST_COMM_SECONDARY_CATEGORY_UUID.toString())
                        .param("title", TEST_POST_TITLE)
                        .param("isPublished", "true")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(postController, times(1))
                .updatePost(argThat(req ->
                        req.ulid().equals(TEST_POST_ULID) &&
                        req.primaryCategoryUuid().equals(TEST_COMM_PRIMARY_CATEGORY_UUID) &&
                        req.secondaryCategoryUuid().equals(TEST_COMM_SECONDARY_CATEGORY_UUID) &&
                        req.title().equals(TEST_POST_TITLE) &&
                        req.content().size() == 2 &&
                        req.orderInfo().size() == 2 &&
                        req.isPublished().equals(true)
                ), any(UUID.class));
    }

    @Test
    @DisplayName("특정 컨텐츠 게시글 삭제하기")
    void testRemovePostByUlid_givenUlid_willRemovePost() throws Exception {
        // given
        willDoNothing().given(postController).deletePost(anyString(), any(UUID.class));

        // when & then
        mockMvc.perform(delete(BASE_URL + "/" + TEST_POST_ULID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("특정 컨텐츠 게시글 조회수 조회하기")
    void testCountViewCount_givenUlid_willReturnViewCount() throws Exception {
        // given
        given(postController.readViewCount(anyString())).willReturn(50L);

        // when & then
        mockMvc.perform(get(BASE_URL + "/" + TEST_POST_ULID + "/views")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(50L));
    }

    @Test
    @DisplayName("특정 컨텐츠 게시글 조회수 증가하기")
    void testIncreaseViewCount_givenUlid_willReturnIncreasedViewCount() throws Exception {
        // given
        given(postController.increaseViewCount(anyString(), any(UUID.class))).willReturn(51L);

        // when & then
        mockMvc.perform(patch(BASE_URL + "/" + TEST_POST_ULID + "/views")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(51L));
    }
}