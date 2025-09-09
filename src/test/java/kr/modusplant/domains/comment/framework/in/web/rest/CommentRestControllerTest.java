package kr.modusplant.domains.comment.framework.in.web.rest;

import kr.modusplant.domains.comment.adapter.controller.CommentController;
import kr.modusplant.domains.comment.adapter.response.CommentResponse;
import kr.modusplant.domains.comment.support.utils.adapter.CommentDeleteRequestTestUtils;
import kr.modusplant.domains.comment.support.utils.adapter.CommentRegisterRequestTestUtils;
import kr.modusplant.domains.comment.support.utils.adapter.CommentResponseTestUtils;
import kr.modusplant.domains.comment.support.utils.domain.PostIdTestUtils;
import kr.modusplant.domains.member.test.utils.domain.MemberIdTestUtils;
import kr.modusplant.framework.out.jackson.holder.ObjectMapperHolder;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static kr.modusplant.framework.out.config.jackson.TestJacksonConfig.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

public class CommentRestControllerTest implements PostIdTestUtils,
        CommentResponseTestUtils, CommentRegisterRequestTestUtils,
        CommentDeleteRequestTestUtils, MemberIdTestUtils {
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    private final CommentController controller = Mockito.mock(CommentController.class);
    private final CommentRestController restController = new CommentRestController(controller);

    @Test
    @DisplayName("게시글에 해당하는 댓글을 가져오는 API 호출")
    public void callGatherByPost_whenValidPostUlid_WillReturnResponseEntity() {
        // given
        given(controller.gatherByPost(testPostId.getId())).willReturn(List.of(testCommentResponse));

        // when
        ResponseEntity<DataResponse<List<CommentResponse>>> result = restController.gatherByPost(testPostId.getId());

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().toString()).isEqualTo(DataResponse.ok(List.of(testCommentResponse)).toString());
    }

    @Test
    @DisplayName("게시글에 해당하는 댓글을 가져오는 API 호출")
    public void callGatherByAuthor_whenValidMemberUuid_WillReturnResponseEntity() {
        // given
        given(controller.gatherByAuthor(testMemberId.getValue())).willReturn(List.of(testCommentResponse));

        // when
        ResponseEntity<DataResponse<List<CommentResponse>>> result = restController.gatherByAuthor(testMemberId.getValue());

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().toString()).isEqualTo(DataResponse.ok(List.of(testCommentResponse)).toString());
    }

    @Test
    @DisplayName("게시글에 해당하는 댓글을 가져오는 API 호출")
    public void callRegister_whenValidRegisterRequest_WillReturnResponseEntity() {
        // given
        doNothing().when(controller).register(testCommentRegisterRequest);

        // when
        ResponseEntity<DataResponse<Void>> result = restController.register(testCommentRegisterRequest);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("게시글에 해당하는 댓글을 가져오는 API 호출")
    public void callDelete_whenValidDeleteRequest_WillReturnResponseEntity() {
        // given
        doNothing().when(controller).delete(testCommentDeleteRequest);

        // when
        ResponseEntity<DataResponse<Void>> result = restController.delete(testCommentDeleteRequest);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
