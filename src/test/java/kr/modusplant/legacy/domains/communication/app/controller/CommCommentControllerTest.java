package kr.modusplant.legacy.domains.communication.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kr.modusplant.legacy.domains.common.context.DomainsControllerOnlyContext;
import kr.modusplant.legacy.domains.communication.app.http.request.CommCommentInsertRequest;
import kr.modusplant.legacy.domains.communication.app.http.response.CommCommentResponse;
import kr.modusplant.legacy.domains.communication.app.service.CommCommentApplicationService;
import kr.modusplant.legacy.domains.communication.common.util.app.http.request.CommCommentInsertRequestTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.app.http.response.CommCommentResponseTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.domain.CommCommentTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.domain.CommPostTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommPostEntityTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.legacy.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberRoleTestUtils;
import kr.modusplant.legacy.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.legacy.modules.jwt.app.service.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DomainsControllerOnlyContext
public class CommCommentControllerTest implements
        CommCommentResponseTestUtils, CommCommentInsertRequestTestUtils, CommCommentTestUtils,
        CommSecondaryCategoryEntityTestUtils, CommPostEntityTestUtils, CommPostTestUtils, SiteMemberRoleTestUtils {

    private final MockMvc mockMvc;

    @Spy
    private final CommCommentApplicationService commentApplicationService;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Autowired
    public CommCommentControllerTest(MockMvc mockMvc, CommCommentApplicationService commentApplicationService) {
        this.mockMvc = mockMvc;
        this.commentApplicationService = commentApplicationService;
    }

    private SiteMemberEntity memberEntity;
    private CommPostEntity postEntity;

    @BeforeEach
    void setUp() {
        memberEntity = createMemberBasicUserEntityWithUuid();
        postEntity = createCommPostEntityBuilder()
                .ulid(TEST_COMM_POST_WITH_ULID.getUlid())
                .secondaryCategory(createTestCommSecondaryCategoryEntityWithUuid())
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
        CommCommentResponse commentResponse =
                createCommCommentResponse(postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname());

        // when
        given(commentApplicationService.getByPostEntity(postEntity)).willReturn(List.of(commentResponse));

        // then
        mockMvc.perform(get("/api/v1/communication/comments/post/{ulid}", postEntity.getUlid()))

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
        CommCommentResponse commentResponse =
                createCommCommentResponse(postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname());

        // when
        given(commentApplicationService.getByAuthMember(memberEntity)).willReturn(List.of(commentResponse));

        // then
        mockMvc.perform(get("/api/v1/communication/comments/member/auth/{uuid}", memberEntity.getUuid()))

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
        CommCommentResponse commentResponse =
                createCommCommentResponse(postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname());

        // when
        given(commentApplicationService.getByCreateMember(memberEntity)).willReturn(List.of(commentResponse));

        // then
        mockMvc.perform(get("/api/v1/communication/comments/member/create/{uuid}", memberEntity.getUuid()))

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
        CommCommentResponse commentResponse =
                createCommCommentResponse(postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname());

        // when
        given(commentApplicationService.getByPostUlidAndPath(postEntity.getUlid(), commentResponse.path()))
                .willReturn(Optional.of(commentResponse));

        // then
        mockMvc.perform(get("/api/v1/communication/comments/post/{ulid}/path/{path}", postEntity.getUlid(), commentResponse.path()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.postUlid").value(postEntity.getUlid()))
                .andExpect(jsonPath("$.data.path").value(commentResponse.path()));
    }

    @DisplayName("댓글 db에 삽입하기")
    @Test
    void insertCommCommentTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        String rawAccessToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        Claims accessTokenClaims = Jwts.claims()
                .subject(memberBasicUserWithUuid.getUuid().toString())
                .add("nickname", memberBasicUserWithUuid.getNickname())
                .add("roles", memberRoleUser.getRole())
                .build();
        CommCommentInsertRequest insertRequest = createCommCommentInsertRequest(postEntity.getUlid());
        CommCommentResponse commentResponse =
                createCommCommentResponse(postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname());

        // when
        given(tokenProvider.getClaimsFromToken(rawAccessToken.substring(7))).willReturn(accessTokenClaims);
        given(commentApplicationService.insert(insertRequest, memberEntity.getUuid())).willReturn(commentResponse);

        // then
        mockMvc.perform(post("/api/v1/communication/comments", insertRequest)
                        .header("Authorization", rawAccessToken)
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
    void removeCommCommentTest() throws Exception {
        // given & when
        doNothing().when(commentApplicationService).removeByPostUlidAndPath(postEntity.getUlid(), TEST_COMM_COMMENT_WITH_POST_ULID_AND_PATH.getPath());

        // then
        mockMvc.perform(delete("/api/v1/communication/comments/post/{ulid}/path/{path}", postEntity.getUlid(), TEST_COMM_COMMENT_WITH_POST_ULID_AND_PATH.getPath()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists());
    }
}
