package kr.modusplant.domains.communication.qna.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.modusplant.domains.common.context.DomainsControllerOnlyContext;
import kr.modusplant.domains.communication.common.app.http.response.PostPageResponse;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostUpdateRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaPostResponse;
import kr.modusplant.domains.communication.qna.app.service.QnaPostApplicationService;
import kr.modusplant.domains.communication.qna.common.util.app.http.request.QnaPostRequestTestUtils;
import kr.modusplant.domains.communication.qna.common.util.app.http.response.QnaPostResponseTestUtils;
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
class QnaPostControllerTest implements QnaPostRequestTestUtils, QnaPostResponseTestUtils {

    private final MockMvc mockMvc;

    private final QnaPostApplicationService qnaPostApplicationService;

    @Autowired
    QnaPostControllerTest(MockMvc mockMvc, QnaPostApplicationService qnaPostApplicationService) {
        this.mockMvc = mockMvc;
        this.qnaPostApplicationService = qnaPostApplicationService;
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    private final String BASE_URL = "/api/v1/qna/posts";
    private final UUID TEST_POST_MEMBER_UUID = UUID.randomUUID();
    private final UUID TEST_CATEGORY_UUID = UUID.randomUUID();
    private final String TEST_POST_ULID = "test-ulid";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 형식으로 출력
    }

    @Test
    @DisplayName("전체 게시글 목록 조회하기")
    void getAllQnaPostsTest() throws Exception {
        // given
        Page<QnaPostResponse> qnaPostResponse = new PageImpl<>(List.of(testQnaPostResponse));
        PostPageResponse<QnaPostResponse> qnaPostPageResponse = PostPageResponse.from(qnaPostResponse);
        when(qnaPostApplicationService.getAll(any(Pageable.class))).thenReturn(qnaPostResponse);

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
                new TypeReference<PostPageResponse<QnaPostResponse>>() {
                })
        ).isEqualTo(qnaPostPageResponse);
    }

    @Test
    @DisplayName("회원별 게시글 목록 조회하기")
    void getQnaPostsByMemberTest() throws Exception {
        // given
        Page<QnaPostResponse> qnaPostResponse = new PageImpl<>(List.of(testQnaPostResponse));
        PostPageResponse<QnaPostResponse> qnaPostPageResponse = PostPageResponse.from(qnaPostResponse);
        when(qnaPostApplicationService.getByMemberUuid(any(UUID.class), any(Pageable.class))).thenReturn(qnaPostResponse);

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
                new TypeReference<PostPageResponse<QnaPostResponse>>() {
                })
        ).isEqualTo(qnaPostPageResponse);
    }

    @Test
    @DisplayName("Q&A 항목별 게시글 목록 조회하기")
    void getQnaPostsByQnaCategoryTest() throws Exception {
        // given
        Page<QnaPostResponse> qnaPostResponse = new PageImpl<>(List.of(testQnaPostResponse));
        PostPageResponse<QnaPostResponse> qnaPostPageResponse = PostPageResponse.from(qnaPostResponse);
        when(qnaPostApplicationService.getByCategoryUuid(any(UUID.class), any(Pageable.class))).thenReturn(qnaPostResponse);

        // when
        Map<String,Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get(BASE_URL + "/category/" + TEST_CATEGORY_UUID)
                                .param("page", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<PostPageResponse<QnaPostResponse>>() {
                })
        ).isEqualTo(qnaPostPageResponse);
    }

    @Test
    @DisplayName("검색어로 게시글 목록 조회하기")
    void searchQnaPostsTest() throws Exception {
        // given
        String keyword = "test";
        Page<QnaPostResponse> qnaPostResponse = new PageImpl<>(List.of(testQnaPostResponse));
        PostPageResponse<QnaPostResponse> qnaPostPageResponse = PostPageResponse.from(qnaPostResponse);
        when(qnaPostApplicationService.searchByKeyword(anyString(), any(Pageable.class))).thenReturn(qnaPostResponse);

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
                new TypeReference<PostPageResponse<QnaPostResponse>>() {
                })
        ).isEqualTo(qnaPostPageResponse);
    }

    @Test
    @DisplayName("ULID로 특정 게시글 조회하기")
    void getQnaPostByUlidTest() throws Exception {
        // given
        when(qnaPostApplicationService.getByUlid(anyString())).thenReturn(Optional.of(testQnaPostResponse));

        // when
        Map<String,Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get(BASE_URL + "/" + TEST_POST_ULID)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<QnaPostResponse>() {
                })
        ).isEqualTo(testQnaPostResponse);
    }

    @Test
    @DisplayName("ULID로 특정 게시글 조회하기 (empty)")
    void getEmptyQnaPostByUlidTest() throws Exception {
        // given
        when(qnaPostApplicationService.getByUlid(anyString())).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get(BASE_URL + "/" + TEST_POST_ULID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 생성하기")
    void insertQnaPostTest() throws Exception {
        // given
        String title = "Test Post Title";
        MockMultipartFile mockTextFile = (MockMultipartFile) textFile0;
        MockMultipartFile mockImageFile = (MockMultipartFile) imageFile;
        MockMultipartFile mockOrderInfoPart = new MockMultipartFile("orderInfo", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(textImageFilesOrder));

        doNothing().when(qnaPostApplicationService).insert(any(QnaPostInsertRequest.class), any(UUID.class));

        // when & then
        mockMvc.perform(multipart(BASE_URL)
                        .file(mockTextFile)
                        .file(mockImageFile)
                        .file(mockOrderInfoPart)
                        .param("categoryUuid", TEST_CATEGORY_UUID.toString())
                        .param("title", title)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(qnaPostApplicationService, times(1))
                .insert(argThat(req ->
                        req.categoryUuid().equals(TEST_CATEGORY_UUID) &&
                                req.title().equals(title) &&
                                req.content().size() == 2 &&
                                req.orderInfo().size() == 2
                ), any(UUID.class));
    }

    @Test
    @DisplayName("게시글 수정하기")
    void updateQnaPostTest() throws Exception {
        // given
        String title = "Test Post Title";
        MockMultipartFile mockTextFile = (MockMultipartFile) textFile0;
        MockMultipartFile mockImageFile = (MockMultipartFile) imageFile;
        MockMultipartFile mockOrderInfoPart = new MockMultipartFile("orderInfo", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(textImageFilesOrder));

        doNothing().when(qnaPostApplicationService).update(any(QnaPostUpdateRequest.class), any(UUID.class));

        // when & then
        mockMvc.perform(multipart(BASE_URL + "/" + TEST_POST_ULID)
                        .file(mockTextFile)
                        .file(mockImageFile)
                        .file(mockOrderInfoPart)
                        .param("categoryUuid", TEST_CATEGORY_UUID.toString())
                        .param("title", title)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(qnaPostApplicationService, times(1))
                .update(argThat(req ->
                        req.ulid().equals(TEST_POST_ULID) &&
                                req.categoryUuid().equals(TEST_CATEGORY_UUID) &&
                                req.title().equals(title) &&
                                req.content().size() == 2 &&
                                req.orderInfo().size() == 2
                ), any(UUID.class));
    }

    @Test
    @DisplayName("ULID로 특정 게시글 삭제하기")
    void removeQnaPostByUlidTest() throws Exception {
        // given
        doNothing().when(qnaPostApplicationService).removeByUlid(anyString(), any(UUID.class));

        // when & then
        mockMvc.perform(delete(BASE_URL + "/" + TEST_POST_ULID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ULID로 특정 게시글의 조회수 구하기")
    void countViewCountTest() throws Exception {
        // given
        when(qnaPostApplicationService.readViewCount(anyString())).thenReturn(50L);

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
        when(qnaPostApplicationService.increaseViewCount(anyString(), any(UUID.class))).thenReturn(51L);

        // when & then
        mockMvc.perform(patch(BASE_URL + "/" + TEST_POST_ULID + "/views")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(51L));
    }
}