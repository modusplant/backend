package kr.modusplant.domains.comment.framework.in.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.comment.adapter.controller.CommentController;
import kr.modusplant.domains.comment.adapter.response.CommentResponse;
import kr.modusplant.domains.comment.support.utils.adapter.CommentDeleteRequestTestUtils;
import kr.modusplant.domains.comment.support.utils.adapter.CommentRegisterRequestTestUtils;
import kr.modusplant.domains.comment.support.utils.adapter.CommentResponseTestUtils;
import kr.modusplant.domains.comment.support.utils.domain.PostIdTestUtils;
import kr.modusplant.domains.member.common.utils.domain.vo.MemberIdTestUtils;
import kr.modusplant.framework.out.jackson.holder.ObjectMapperHolder;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

public class CommentRestControllerTest implements PostIdTestUtils,
        CommentResponseTestUtils, CommentRegisterRequestTestUtils,
        CommentDeleteRequestTestUtils, MemberIdTestUtils {
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(new ObjectMapper());
    private final CommentController controller = Mockito.mock(CommentController.class);
    private final CommentRestController restController = new CommentRestController(controller);

    @Test
    @DisplayName("유효한 게시글 id로 게시글의 댓글 가져오기")
    public void testGatherByPost_givenValidPostUlid_WillReturnResponseEntity() {
        // given
        given(controller.gatherByPost(testPostId.getId())).willReturn(List.of(testCommentResponse));

        // when
        ResponseEntity<DataResponse<List<CommentResponse>>> result = restController.gatherByPost(testPostId.getId());

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().toString()).isEqualTo(DataResponse.ok(List.of(testCommentResponse)).toString());
    }

    @Test
    @DisplayName("유효한 작성자 id로 작성자에 해당하는 댓글 가져오기")
    public void testGatherByAuthor_givenValidMemberUuid_WillReturnResponseEntity() {
        // given
        given(controller.gatherByAuthor(testMemberId.getValue())).willReturn(List.of(testCommentResponse));

        // when
        ResponseEntity<DataResponse<List<CommentResponse>>> result = restController.gatherByAuthor(testMemberId.getValue());

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().toString()).isEqualTo(DataResponse.ok(List.of(testCommentResponse)).toString());
    }

    @Test
    @DisplayName("유효한 댓글 등록 객체로 댓글 저장")
    public void testRegister_givenValidRegisterRequest_WillReturnResponseEntity() {
        // given
        doNothing().when(controller).register(testCommentRegisterRequest);

        // when
        ResponseEntity<DataResponse<Void>> result = restController.register(testCommentRegisterRequest);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("유효한 삭제 요청으로 댓글 삭제")
    public void testDelete_givenValidDeleteRequest_WillReturnResponseEntity() {
        // given
        doNothing().when(controller).delete(testCommentDeleteRequest);

        // when
        ResponseEntity<DataResponse<Void>> result = restController.delete(testCommentDeleteRequest);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
