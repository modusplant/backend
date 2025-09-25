package kr.modusplant.domains.comment.adapter.controller;

import kr.modusplant.domains.comment.adapter.mapper.CommentMapperImpl;
import kr.modusplant.domains.comment.usecase.port.repository.CommentAuthorRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentRepository;
import kr.modusplant.domains.comment.usecase.response.CommentResponse;
import kr.modusplant.domains.comment.support.utils.adapter.CommentReadModelTestUtils;
import kr.modusplant.domains.comment.support.utils.adapter.CommentResponseTestUtils;
import kr.modusplant.domains.comment.support.utils.adapter.MemberReadModelTestUtils;
import kr.modusplant.domains.comment.support.utils.domain.AuthorTestUtils;
import kr.modusplant.domains.comment.support.utils.domain.PostIdTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class CommentControllerTest implements PostIdTestUtils, AuthorTestUtils,
        CommentReadModelTestUtils, MemberReadModelTestUtils, CommentResponseTestUtils {
    private final CommentMapperImpl mapper = Mockito.mock(CommentMapperImpl.class);
    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    private final CommentAuthorRepository authorRepository = Mockito.mock(CommentAuthorRepository.class);
    private final CommentController controller = new CommentController(mapper, commentRepository);

    @Test
    @DisplayName("유효한 게시글 id로 댓글 읽기")
    public void testGatherByPost_givenValidPostUlid_willReturnResponseList() {
        // given
        given(commentRepository.findByPost(testPostId)).willReturn(List.of(testCommentResponse));
        given(authorRepository.findByAuthor(testAuthorWithUuid)).willReturn(testMemberReadModel);

        // when
        List<CommentResponse> result = controller.gatherByPost(testPostId.getId());

        // then
        assertThat(result).isEqualTo(List.of(testCommentResponse));
    }

    @Test
    @DisplayName("유효한 작성자 id로 댓글 읽기")
    public void testGatherByAuthor_givenValidPostUlid_willReturnResponseList() {
        // given
        given(commentRepository.findByAuthor(testAuthorWithUuid)).willReturn(List.of(testCommentResponse));
        given(authorRepository.findByAuthor(testAuthorWithUuid)).willReturn(testMemberReadModel);

        // when
        List<CommentResponse> result = controller.gatherByAuthor(testAuthorWithUuid.getMemberUuid());

        // then
        assertThat(result).isEqualTo(List.of(testCommentResponse));
    }
}
