package kr.modusplant.domains.post.framework.in.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.modusplant.domains.post.adapter.controller.PostController;
import kr.modusplant.domains.post.common.util.usecase.request.PostRequestTestUtils;
import kr.modusplant.domains.post.common.util.usecase.response.PostResponseTestUtils;
import kr.modusplant.domains.post.usecase.request.PostFilterRequest;
import kr.modusplant.domains.post.usecase.request.PostInsertRequest;
import kr.modusplant.domains.post.usecase.request.PostUpdateRequest;
import kr.modusplant.domains.post.usecase.response.PostDetailResponse;
import kr.modusplant.domains.post.usecase.response.PostSummaryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import static kr.modusplant.domains.post.common.constant.PostUuidConstant.TEST_POST_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    private static final int PAGE_SIZE = 10;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postRestController).build();
        ReflectionTestUtils.setField(postRestController, "currentMemberUuid", TEST_POST_UUID);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 형식으로 출력
    }

    @Test
    @DisplayName("전체 컨텐츠 게시글 목록 조회하기")
    void testGetAllPosts_givenPage_willReturnPosts() throws Exception {
        // given
        Page<PostSummaryResponse> postSummaryResponses = new PageImpl<>(List.of(TEST_POST_SUMMARY_RESPONSE));
        given(postController.getAll(any(PostFilterRequest.class), any(PageRequest.class))).willReturn(postSummaryResponses);

        // when & then
        mockMvc.perform(get(BASE_URL)
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(postController).getAll(
                argThat(filter ->
                        filter.primaryCategoryUuid() == null &&
                                filter.secondaryCategoryUuids() == null &&
                                filter.keyword() == null
                ),
                argThat(pageRequest ->
                        pageRequest.getPageNumber() == 0 &&
                                pageRequest.getPageSize() == PAGE_SIZE
                )
        );
    }

    @Test
    @DisplayName("1차 카테고리로 전체 컨텐츠 게시글 목록 조회하기")
    void testGetAllPosts_givenPrimaryCategoryAndPage_willReturnPosts() throws Exception {
        // given
        Page<PostSummaryResponse> postSummaryResponses = new PageImpl<>(List.of(TEST_POST_SUMMARY_RESPONSE));
        given(postController.getAll(any(PostFilterRequest.class), any(PageRequest.class))).willReturn(postSummaryResponses);

        // when & then
        mockMvc.perform(get(BASE_URL)
                        .param("primary_category_id", TEST_COMM_PRIMARY_CATEGORY_UUID.toString())
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(postController).getAll(
                argThat(filter ->
                        filter.primaryCategoryUuid().equals(TEST_COMM_PRIMARY_CATEGORY_UUID) &&
                                filter.secondaryCategoryUuids() == null &&
                                filter.keyword() == null
                ),
                any(PageRequest.class)
        );
    }

    @Test
    @DisplayName("2차 카테고리로 전체 컨텐츠 게시글 목록 조회하기")
    void testGetAllPosts_givenSecondaryCategoryAndPage_willReturnPosts() throws Exception {
        // given
        UUID secondaryUuid1 = TEST_COMM_SECONDARY_CATEGORY_UUID;
        UUID secondaryUuid2 = UUID.randomUUID();
        Page<PostSummaryResponse> postSummaryResponses = new PageImpl<>(List.of(TEST_POST_SUMMARY_RESPONSE));
        given(postController.getAll(any(PostFilterRequest.class), any(PageRequest.class))).willReturn(postSummaryResponses);

        // when & then
        mockMvc.perform(get(BASE_URL)
                        .param("secondary_category_id", secondaryUuid1.toString())
                        .param("secondary_category_id", secondaryUuid2.toString())
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(postController).getAll(
                argThat(filter ->
                        filter.secondaryCategoryUuids() != null &&
                                filter.secondaryCategoryUuids().size() == 2 &&
                                filter.secondaryCategoryUuids().contains(secondaryUuid1) &&
                                filter.secondaryCategoryUuids().contains(secondaryUuid2)
                ),
                any(PageRequest.class)
        );
    }

    @Test
    @DisplayName("{제목+본문} 키워드로 전체 컨텐츠 게시글 목록 조회하기")
    void testGetAllPosts_givenKeywordAndPage_willReturnPosts() throws Exception {
        // given
        String keyword = "벌레";
        Page<PostSummaryResponse> postSummaryResponses = new PageImpl<>(List.of(TEST_POST_SUMMARY_RESPONSE));
        given(postController.getAll(any(PostFilterRequest.class), any(PageRequest.class))).willReturn(postSummaryResponses);

        // when & then
        mockMvc.perform(get(BASE_URL)
                        .param("keyword", keyword)
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(postController).getAll(
                argThat(filter ->
                        filter.keyword().equals(keyword)
                ),
                any(PageRequest.class)
        );
    }

    @Test
    @DisplayName("전체 필터로 전체 컨텐츠 게시글 목록 조회하기")
    void testGetAllPosts_givenFilterAndPage_willReturnPosts() throws Exception {
        // given
        String keyword = "식물";
        Page<PostSummaryResponse> postSummaryResponses = new PageImpl<>(List.of(TEST_POST_SUMMARY_RESPONSE));
        given(postController.getAll(any(PostFilterRequest.class), any(PageRequest.class))).willReturn(postSummaryResponses);

        // when & then
        mockMvc.perform(get(BASE_URL)
                        .param("primary_category_id", TEST_COMM_PRIMARY_CATEGORY_UUID.toString())
                        .param("secondary_category_id", TEST_COMM_SECONDARY_CATEGORY_UUID.toString())
                        .param("keyword", keyword)
                        .param("page", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(postController).getAll(
                argThat(filter ->
                        filter.primaryCategoryUuid().equals(TEST_COMM_PRIMARY_CATEGORY_UUID) &&
                                filter.secondaryCategoryUuids().contains(TEST_COMM_SECONDARY_CATEGORY_UUID) &&
                                filter.keyword().equals(keyword)
                ),
                argThat(pageRequest ->
                        pageRequest.getPageNumber() == 1 && // page 2 = index 1
                                pageRequest.getPageSize() == PAGE_SIZE
                )
        );
    }

    @Test
    @DisplayName("사이트 회원별 컨텐츠 게시글 목록 조회하기")
    void testGetPostsByMember_givenMemberUuidAndPage_willReturnPosts() throws Exception {
        // given
        UUID testMemberUuid = UUID.randomUUID();
        Page<PostSummaryResponse> postSummaryResponses = new PageImpl<>(List.of(TEST_POST_SUMMARY_RESPONSE));
        given(postController.getByMemberUuid(any(UUID.class), any(PostFilterRequest.class), any(PageRequest.class))).willReturn(postSummaryResponses);

        // when & then
        mockMvc.perform(get(BASE_URL + "/member/" + testMemberUuid)
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(postController).getByMemberUuid(
                eq(testMemberUuid),
                argThat(filter ->
                        filter.primaryCategoryUuid() == null &&
                                filter.secondaryCategoryUuids() == null &&
                                filter.keyword() == null
                ),
                argThat(pageRequest ->
                        pageRequest.getPageNumber() == 0 &&
                                pageRequest.getPageSize() == PAGE_SIZE
                )
        );
    }

    @Test
    @DisplayName("전체 필터로 사이트 회원별 컨텐츠 게시글 목록 조회하기")
    void testGetPostsByMember_givenMemberUuidAndFilterAndPage_willReturnPosts() throws Exception {
        // given
        UUID testMemberUuid = UUID.randomUUID();
        UUID secondaryUuid = UUID.randomUUID();
        String keyword = "식물";
        Page<PostSummaryResponse> postSummaryResponses = new PageImpl<>(List.of(TEST_POST_SUMMARY_RESPONSE));
        given(postController.getByMemberUuid(any(UUID.class), any(PostFilterRequest.class), any(PageRequest.class))).willReturn(postSummaryResponses);

        // when & then
        mockMvc.perform(get(BASE_URL + "/member/" + testMemberUuid)
                        .param("primary_category_id", TEST_COMM_PRIMARY_CATEGORY_UUID.toString())
                        .param("secondary_category_id", TEST_COMM_SECONDARY_CATEGORY_UUID.toString())
                        .param("secondary_category_id", secondaryUuid.toString())
                        .param("keyword", keyword)
                        .param("page", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(postController).getByMemberUuid(
                eq(testMemberUuid),
                argThat(filter ->
                        filter.primaryCategoryUuid().equals(TEST_COMM_PRIMARY_CATEGORY_UUID) &&
                                filter.secondaryCategoryUuids() != null &&
                                filter.secondaryCategoryUuids().size() == 2 &&
                                filter.secondaryCategoryUuids().contains(TEST_COMM_SECONDARY_CATEGORY_UUID) &&
                                filter.secondaryCategoryUuids().contains(secondaryUuid) &&
                                filter.keyword().equals(keyword)
                ),
                argThat(pageRequest ->
                        pageRequest.getPageNumber() == 1 &&
                                pageRequest.getPageSize() == PAGE_SIZE
                )
        );
    }

    @Test
    @DisplayName("로그인한 회원의 임시저장된 게시글 목록 조회하기")
    void testGetDraftPostsByMember_givenPage_willReturnDraftPosts() throws Exception {
        // given
        Page<PostSummaryResponse> postSummaryResponses = new PageImpl<>(List.of(TEST_POST_SUMMARY_RESPONSE));
        given(postController.getDraftByMemberUuid(any(UUID.class), any(PageRequest.class))).willReturn(postSummaryResponses);

        // when & then
        mockMvc.perform(get(BASE_URL + "/me/drafts")
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(postController).getDraftByMemberUuid(
                any(UUID.class),
                argThat(pageRequest ->
                        pageRequest.getPageNumber() == 0 &&
                                pageRequest.getPageSize() == PAGE_SIZE
                )
        );
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
        MockMultipartFile mockOrderInfoPart = new MockMultipartFile("order_info", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(textImageFilesOrder));

        willDoNothing().given(postController).createPost(any(PostInsertRequest.class),any(UUID.class));

        // when & then
        mockMvc.perform(multipart(BASE_URL)
                        .file(mockTextFile)
                        .file(mockImageFile)
                        .file(mockOrderInfoPart)
                        .param("primary_category_id", TEST_COMM_PRIMARY_CATEGORY_UUID.toString())
                        .param("secondary_category_id", TEST_COMM_SECONDARY_CATEGORY_UUID.toString())
                        .param("title", TEST_POST_TITLE)
                        .param("is_published", "true")
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
        MockMultipartFile mockOrderInfoPart = new MockMultipartFile("order_info", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(textImageFilesOrder));

        willDoNothing().given(postController).updatePost(any(PostUpdateRequest.class),any(UUID.class));

        // when & then
        mockMvc.perform(multipart(BASE_URL + "/" + TEST_POST_ULID)
                        .file(mockTextFile)
                        .file(mockImageFile)
                        .file(mockOrderInfoPart)
                        .param("primary_category_id", TEST_COMM_PRIMARY_CATEGORY_UUID.toString())
                        .param("secondary_category_id", TEST_COMM_SECONDARY_CATEGORY_UUID.toString())
                        .param("title", TEST_POST_TITLE)
                        .param("is_published", "true")
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