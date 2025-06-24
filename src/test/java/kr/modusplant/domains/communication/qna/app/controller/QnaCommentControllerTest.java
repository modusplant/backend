package kr.modusplant.domains.communication.qna.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.common.context.DomainsControllerOnlyContext;
import kr.modusplant.domains.communication.qna.app.http.request.QnaCommentInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaCommentResponse;
import kr.modusplant.domains.communication.qna.app.service.QnaCommentApplicationService;
import kr.modusplant.domains.communication.qna.common.util.app.http.request.QnaCommentInsertRequestTestUtils;
import kr.modusplant.domains.communication.qna.common.util.app.http.response.QnaCommentResponseTestUtils;
import kr.modusplant.domains.communication.qna.common.util.domain.QnaCommentTestUtils;
import kr.modusplant.domains.communication.qna.common.util.domain.QnaPostTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
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
public class QnaCommentControllerTest implements
        QnaCommentResponseTestUtils, QnaCommentInsertRequestTestUtils, QnaCommentTestUtils,
        QnaCategoryEntityTestUtils, QnaPostEntityTestUtils, QnaPostTestUtils {

    private final MockMvc mockMvc;

    @Spy
    private final QnaCommentApplicationService commentApplicationService;

    @Autowired
    public QnaCommentControllerTest(MockMvc mockMvc, QnaCommentApplicationService commentApplicationService) {
        this.mockMvc = mockMvc;
        this.commentApplicationService = commentApplicationService;
    }

    private SiteMemberEntity memberEntity;
    private QnaPostEntity postEntity;

    @BeforeEach
    void setUp() {
        memberEntity = createMemberBasicUserEntityWithUuid();
        postEntity = createQnaPostEntityBuilder()
                .ulid(testQnaPostWithUlid.getUlid())
                .category(createTestQnaCategoryEntityWithUuid())
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
        QnaCommentResponse commentResponse =
                createQnaCommentResponse(postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname());

        // when
        given(commentApplicationService.getByPostEntity(postEntity)).willReturn(List.of(commentResponse));

        // then
        mockMvc.perform(get("/api/v1/qna/comments/post/{ulid}", postEntity.getUlid()))

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
        QnaCommentResponse commentResponse =
                createQnaCommentResponse(postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname());

        // when
        given(commentApplicationService.getByAuthMember(memberEntity)).willReturn(List.of(commentResponse));

        // then
        mockMvc.perform(get("/api/v1/qna/comments/member/auth/{uuid}", memberEntity.getUuid()))

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
        QnaCommentResponse commentResponse =
                createQnaCommentResponse(postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname());

        // when
        given(commentApplicationService.getByCreateMember(memberEntity)).willReturn(List.of(commentResponse));

        // then
        mockMvc.perform(get("/api/v1/qna/comments/member/create/{uuid}", memberEntity.getUuid()))

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
        QnaCommentResponse commentResponse =
                createQnaCommentResponse(postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname());

        // when
        given(commentApplicationService.getByPostUlidAndPath(postEntity.getUlid(), commentResponse.path()))
                .willReturn(Optional.of(commentResponse));

        // then
        mockMvc.perform(get("/api/v1/qna/comments/post/{ulid}/path/{path}", postEntity.getUlid(), commentResponse.path()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.postUlid").value(postEntity.getUlid()))
                .andExpect(jsonPath("$.data.path").value(commentResponse.path()));
    }

    @DisplayName("댓글 db에 삽입하기")
    @Test
    void insertQnaCommentTest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        QnaCommentInsertRequest insertRequest = createQnaCommentInsertRequest(postEntity.getUlid());
        QnaCommentResponse commentResponse =
                createQnaCommentResponse(postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname());

        // when
        given(commentApplicationService.insert(insertRequest, memberEntity.getUuid())).willReturn(commentResponse);

        // then
        mockMvc.perform(post("/api/v1/qna/comments", insertRequest)
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
    void removeQnaCommentTest() throws Exception {
        // given & when
        doNothing().when(commentApplicationService).removeByPostUlidAndPath(postEntity.getUlid(), testQnaCommentWithPostUlidAndPath.getPath());

        // then
        mockMvc.perform(delete("/api/v1/qna/comments/post/{ulid}/path/{path}", postEntity.getUlid(), testQnaCommentWithPostUlidAndPath.getPath()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists());
    }
}
