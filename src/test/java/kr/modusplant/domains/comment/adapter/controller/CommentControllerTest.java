package kr.modusplant.domains.comment.adapter.controller;

import kr.modusplant.domains.comment.adapter.mapper.CommentMapperImpl;
import kr.modusplant.domains.comment.common.util.adapter.CommentReadModelTestUtils;
import kr.modusplant.domains.comment.common.util.adapter.CommentResponseTestUtils;
import kr.modusplant.domains.comment.common.util.adapter.MemberReadModelTestUtils;
import kr.modusplant.domains.comment.common.util.domain.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.framework.out.persistence.jooq.CommentRepositoryJooqAdapter;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentRepositoryJpaAdapter;
import kr.modusplant.domains.comment.usecase.response.CommentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class CommentControllerTest implements PostIdTestUtils, AuthorTestUtils,
        CommentReadModelTestUtils, MemberReadModelTestUtils, CommentResponseTestUtils {
    private final CommentMapperImpl mapper = Mockito.mock(CommentMapperImpl.class);
    private final CommentRepositoryJooqAdapter jooqAdapter = Mockito.mock(CommentRepositoryJooqAdapter.class);
    private final CommentRepositoryJpaAdapter jpaAdapter = Mockito.mock(CommentRepositoryJpaAdapter.class);
    private final CommentController controller = new CommentController(mapper, jooqAdapter, jpaAdapter);

    @Test
    @DisplayName("유효한 게시글 id로 댓글 읽기")
    public void testGatherByPost_givenValidPostUlid_willReturnResponseList() {
        // given
        given(jooqAdapter.findByPost(testPostId)).willReturn(List.of(testCommentResponse));

        // when
        List<CommentResponse> result = controller.gatherByPost(testPostId.getId());

        // then
        assertThat(result).isEqualTo(List.of(testCommentResponse));
    }

    @Test
    @DisplayName("유효한 작성자 id로 댓글 읽기")
    public void testGatherByAuthor_givenValidPostUlid_willReturnResponseList() {
        // given
        given(jooqAdapter.findByAuthor(testAuthorWithUuid)).willReturn(List.of(testCommentResponse));

        // when
        List<CommentResponse> result = controller.gatherByAuthor(testAuthorWithUuid.getMemberUuid());

        // then
        assertThat(result).isEqualTo(List.of(testCommentResponse));
    }
}
