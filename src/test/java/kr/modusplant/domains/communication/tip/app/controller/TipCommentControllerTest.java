package kr.modusplant.domains.communication.tip.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.common.context.DomainsControllerOnlyContext;
import kr.modusplant.domains.communication.tip.app.http.request.TipCommentInsertRequest;
import kr.modusplant.domains.communication.tip.app.http.response.TipCommentResponse;
import kr.modusplant.domains.communication.tip.app.service.TipCommentApplicationService;
import kr.modusplant.domains.communication.tip.common.util.app.http.request.TipCommentInsertRequestTestUtils;
import kr.modusplant.domains.communication.tip.common.util.app.http.response.TipCommentResponseTestUtils;
import kr.modusplant.domains.communication.tip.common.util.domain.TipCommentTestUtils;
import kr.modusplant.domains.communication.tip.common.util.domain.TipPostTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCategoryEntityTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DomainsControllerOnlyContext
public class TipCommentControllerTest implements
        TipCommentResponseTestUtils, TipCommentInsertRequestTestUtils, TipCommentTestUtils,
        TipCategoryEntityTestUtils, TipPostEntityTestUtils, TipPostTestUtils {

    private final MockMvc mockMvc;

    @Spy
    private final TipCommentApplicationService commentApplicationService;

    @Autowired
    public TipCommentControllerTest(MockMvc mockMvc, TipCommentApplicationService commentApplicationService) {
        this.mockMvc = mockMvc;
        this.commentApplicationService = commentApplicationService;
    }

    private SiteMemberEntity memberEntity;
    private TipPostEntity postEntity;

    @BeforeEach
    void setUp() {
        memberEntity = createMemberBasicUserEntityWithUuid();
        postEntity = createTipPostEntityBuilder()
                .ulid(testTipPostWithUlid.getUlid())
                .category(createTestTipCategoryEntityWithUuid())
                .authMember(memberEntity)
                .createMember(memberEntity)
                .likeCount(1)
                .viewCount(1L)
                .isDeleted(true)
                .build();
    }

    @DisplayName("게시글로 댓글 얻기")
    @Test
    void getByPostTest() throws Exception {
        // given
        TipCommentResponse commentResponse =
                createTipCommentResponse(postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname());

        // when
        given(commentApplicationService.getByPostEntity(postEntity)).willReturn(List.of(commentResponse));

        // then
        mockMvc.perform(get("/api/v1/tip/comments/post/{ulid}", postEntity.getUlid()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[*].postUlid").value(postEntity.getUlid()))
                .andExpect(jsonPath("$.data[*].memberUuid").value(memberEntity.getUuid().toString()));
    }

    @DisplayName("인증된 사용자로 댓글 얻기")
    @Test
    void getByAuthMemberTest() throws Exception {
        // given
        TipCommentResponse commentResponse =
                createTipCommentResponse(postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname());

        // when
        given(commentApplicationService.getByAuthMember(memberEntity)).willReturn(List.of(commentResponse));

        // then
        mockMvc.perform(get("/api/v1/tip/comments/member/auth/{uuid}", memberEntity.getUuid()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[*].postUlid").value(postEntity.getUlid()))
                .andExpect(jsonPath("$.data[*].memberUuid").value(memberEntity.getUuid().toString()));
    }

    @DisplayName("생성한 사용자로 댓글 얻기")
    @Test
    void getByCreateMemberTest() throws Exception {
        // given
        TipCommentResponse commentResponse =
                createTipCommentResponse(postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname());

        // when
        given(commentApplicationService.getByCreateMember(memberEntity)).willReturn(List.of(commentResponse));

        // then
        mockMvc.perform(get("/api/v1/tip/comments/member/create/{uuid}", memberEntity.getUuid()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[*].postUlid").value(postEntity.getUlid()))
                .andExpect(jsonPath("$.data[*].nickname").value(memberEntity.getNickname()));
    }

    @DisplayName("게시글 ulid와 댓글 경로로 댓글 얻기")
    @Test
    void getByPostAndPathTest() throws Exception {
        // given
        TipCommentResponse commentResponse =
                createTipCommentResponse(postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname());

        // when
        given(commentApplicationService.getByPostUlidAndPath(postEntity.getUlid(), commentResponse.path()))
                .willReturn(Optional.of(commentResponse));

        // then
        mockMvc.perform(get("/api/v1/tip/comments/post/{ulid}/path/{path}", postEntity.getUlid(), commentResponse.path()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.postUlid").value(postEntity.getUlid()))
                .andExpect(jsonPath("$.data.path").value(commentResponse.path()));
    }

    @DisplayName("댓글 db에 삽입하기")
    @Test
    void insertTipCommentTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        TipCommentInsertRequest insertRequest = createTipCommentInsertRequest(postEntity.getUlid());
        TipCommentResponse commentResponse =
                createTipCommentResponse(postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname());

        // when
        given(commentApplicationService.insert(insertRequest, memberEntity.getUuid())).willReturn(commentResponse);

        // then
        mockMvc.perform(post("/api/v1/tip/comments", insertRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertRequest)).characterEncoding("UTF-8"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.postUlid").value(postEntity.getUlid()))
                .andExpect(jsonPath("$.data.path").value(commentResponse.path()));
    }

    @DisplayName("게시글 ulid와 댓글 경로로 댓글 삭제하기")
    @Test
    void removeTipCommentTest() throws Exception {
        // given & when
        doNothing().when(commentApplicationService).removeByPostUlidAndPath(postEntity.getUlid(), tipCommentWithPostUlidAndPath.getPath());

        // then
        mockMvc.perform(delete("/api/v1/tip/comments/post/{ulid}/path/{path}", postEntity.getUlid(), tipCommentWithPostUlidAndPath.getPath()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists());
    }
}
