package kr.modusplant.domains.communication.tip.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.modusplant.domains.common.context.DomainsControllerOnlyContext;
import kr.modusplant.domains.communication.common.app.http.response.PostPageResponse;
import kr.modusplant.domains.communication.tip.app.http.request.TipPostInsertRequest;
import kr.modusplant.domains.communication.tip.app.http.request.TipPostUpdateRequest;
import kr.modusplant.domains.communication.tip.app.http.response.TipPostResponse;
import kr.modusplant.domains.communication.tip.app.service.TipPostApplicationService;
import kr.modusplant.domains.communication.tip.common.util.app.http.request.TipPostRequestTestUtils;
import kr.modusplant.domains.communication.tip.common.util.app.http.response.TipPostResponseTestUtils;
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
import static kr.modusplant.global.vo.DatabaseFieldName.CATE_UUID;
import static kr.modusplant.global.vo.DatabaseFieldName.ORDER_INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DomainsControllerOnlyContext
class TipPostControllerTest implements TipPostRequestTestUtils, TipPostResponseTestUtils {

    private final MockMvc mockMvc;

    private TipPostApplicationService tipPostApplicationService;

    @Autowired
    TipPostControllerTest(MockMvc mockMvc, TipPostApplicationService tipPostApplicationService) {
        this.mockMvc = mockMvc;
        this.tipPostApplicationService = tipPostApplicationService;
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    private final String BASE_URL = "/api/v1/tip/posts";
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
    void getAllTipPostsTest() throws Exception {
        // given
        Page<TipPostResponse> tipPostResponse = new PageImpl<>(List.of(testTipPostResponse));
        PostPageResponse<TipPostResponse> tipPostPageResponse = PostPageResponse.from(tipPostResponse);
        when(tipPostApplicationService.getAll(any(Pageable.class))).thenReturn(tipPostResponse);

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
                new TypeReference<PostPageResponse<TipPostResponse>>() {
                })
        ).isEqualTo(tipPostPageResponse);
    }

    @Test
    @DisplayName("회원별 게시글 목록 조회하기")
    void getTipPostsByMemberTest() throws Exception {
        // given
        Page<TipPostResponse> tipPostResponse = new PageImpl<>(List.of(testTipPostResponse));
        PostPageResponse<TipPostResponse> tipPostPageResponse = PostPageResponse.from(tipPostResponse);
        when(tipPostApplicationService.getByMemberUuid(any(UUID.class), any(Pageable.class))).thenReturn(tipPostResponse);

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
                new TypeReference<PostPageResponse<TipPostResponse>>() {
                })
        ).isEqualTo(tipPostPageResponse);
    }

    @Test
    @DisplayName("팁 항목별 게시글 목록 조회하기")
    void getTipPostsByTipCategoryTest() throws Exception {
        // given
        Page<TipPostResponse> tipPostResponse = new PageImpl<>(List.of(testTipPostResponse));
        PostPageResponse<TipPostResponse> tipPostPageResponse = PostPageResponse.from(tipPostResponse);
        when(tipPostApplicationService.getByCategoryUuid(any(UUID.class), any(Pageable.class))).thenReturn(tipPostResponse);

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
                new TypeReference<PostPageResponse<TipPostResponse>>() {
                })
        ).isEqualTo(tipPostPageResponse);
    }

    @Test
    @DisplayName("검색어로 게시글 목록 조회하기")
    void searchTipPostsTest() throws Exception {
        // given
        String keyword = "test";
        Page<TipPostResponse> tipPostResponse = new PageImpl<>(List.of(testTipPostResponse));
        PostPageResponse<TipPostResponse> tipPostPageResponse = PostPageResponse.from(tipPostResponse);
        when(tipPostApplicationService.searchByKeyword(anyString(), any(Pageable.class))).thenReturn(tipPostResponse);

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
                new TypeReference<PostPageResponse<TipPostResponse>>() {
                })
        ).isEqualTo(tipPostPageResponse);
    }

    @Test
    @DisplayName("ULID로 특정 게시글 조회하기")
    void getTipPostByUlidTest() throws Exception {
        // given
        when(tipPostApplicationService.getByUlid(anyString())).thenReturn(Optional.of(testTipPostResponse));

        // when
        Map<String,Object> responseMap = objectMapper.readValue(
                mockMvc.perform(get(BASE_URL + "/" + TEST_POST_ULID)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(), new TypeReference<>() {
                });

        // then
        assertThat(objectMapper.convertValue(responseMap.get(DATA),
                new TypeReference<TipPostResponse>() {
                })
        ).isEqualTo(testTipPostResponse);
    }

    @Test
    @DisplayName("ULID로 특정 게시글 조회하기 (empty)")
    void getEmptyTipPostByUlidTest() throws Exception {
        // given
        when(tipPostApplicationService.getByUlid(anyString())).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get(BASE_URL + "/" + TEST_POST_ULID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 생성하기")
    void insertTipPostTest() throws Exception {
        // given
        String title = "Test Post Title";
        MockMultipartFile mockTextFile = (MockMultipartFile) textFile0;
        MockMultipartFile mockImageFile = (MockMultipartFile) imageFile;
        MockMultipartFile mockOrderInfoPart = new MockMultipartFile(ORDER_INFO, "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(textImageFilesOrder));

        doNothing().when(tipPostApplicationService).insert(any(TipPostInsertRequest.class), any(UUID.class));

        // when & then
        mockMvc.perform(multipart(BASE_URL)
                        .file(mockTextFile)
                        .file(mockImageFile)
                        .file(mockOrderInfoPart)
                        .param(CATE_UUID, TEST_CATEGORY_UUID.toString())
                        .param("title", title)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(tipPostApplicationService, times(1))
                .insert(argThat(req ->
                        req.categoryUuid().equals(TEST_CATEGORY_UUID) &&
                                req.title().equals(title) &&
                                req.content().size() == 2 &&
                                req.orderInfo().size() == 2
                ), any(UUID.class));
    }

    @Test
    @DisplayName("게시글 수정하기")
    void updateTipPostTest() throws Exception {
        // given
        String title = "Test Post Title";
        MockMultipartFile mockTextFile = (MockMultipartFile) textFile0;
        MockMultipartFile mockImageFile = (MockMultipartFile) imageFile;
        MockMultipartFile mockOrderInfoPart = new MockMultipartFile(ORDER_INFO, "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(textImageFilesOrder));

        doNothing().when(tipPostApplicationService).update(any(TipPostUpdateRequest.class), any(UUID.class));

        // when & then
        mockMvc.perform(multipart(BASE_URL + "/" + TEST_POST_ULID)
                        .file(mockTextFile)
                        .file(mockImageFile)
                        .file(mockOrderInfoPart)
                        .param(CATE_UUID, TEST_CATEGORY_UUID.toString())
                        .param("title", title)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(tipPostApplicationService, times(1))
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
    void removeTipPostByUlidTest() throws Exception {
        // given
        doNothing().when(tipPostApplicationService).removeByUlid(anyString(), any(UUID.class));

        // when & then
        mockMvc.perform(delete(BASE_URL + "/" + TEST_POST_ULID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ULID로 특정 게시글의 조회수 구하기")
    void countViewCountTest() throws Exception {
        // given
        when(tipPostApplicationService.readViewCount(anyString())).thenReturn(50L);

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
        when(tipPostApplicationService.increaseViewCount(anyString(), any(UUID.class))).thenReturn(51L);

        // when & then
        mockMvc.perform(patch(BASE_URL + "/" + TEST_POST_ULID + "/views")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(51L));
    }

}