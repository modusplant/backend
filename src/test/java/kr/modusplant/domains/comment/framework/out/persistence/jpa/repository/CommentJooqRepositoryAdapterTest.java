package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.common.util.adapter.CommentResponseTestUtils;
import kr.modusplant.domains.comment.common.util.domain.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers.CommentJooqRepository;
import kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.CommCommentEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.CommCommentIdTestUtils;
import kr.modusplant.jooq.tables.CommComment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

public class CommentJooqRepositoryAdapterTest implements PostIdTestUtils,
        CommentResponseTestUtils, MemberIdTestUtils,
        AuthorTestUtils, CommentTestUtils,
        CommCommentEntityTestUtils, CommCommentIdTestUtils {
    private final CommentJooqRepository repository = Mockito.mock(CommentJooqRepository.class);
    private final CommentRepositoryJooqAdapter adapter = new CommentRepositoryJooqAdapter(repository);

//    @Test
//    @DisplayName("게시글 식별자로 댓글 가져오기")
//    public void testFindByPost_givenValidPostUlid_willGetComment() {
//        // given
//        given(repository.findByPostUlid(testPostId.getId())).willReturn(List.of(testCommentResponse));
//
//        // when
//        adapter.findByPost(testPostId);
//
//        // then
//        Mockito.verify(repository, times(1)).findByPostUlid(testPostId.getId());
//    }
//
//    @Test
//    @DisplayName("사용자의 식별자로 댓글 가져오기")
//    public void testFindByAuthor_givenValidAuthor_willGetComment() {
//        // given
//        given(repository.findByAuthMemberUuid(testAuthor.getMemberUuid())).willReturn(List.of(testCommentResponse));
//
//        // when
//        adapter.findByAuthor(testAuthor);
//
//        // then
//        Mockito.verify(repository, times(1)).findByAuthMemberUuid(testAuthor.getMemberUuid());
//    }
}
