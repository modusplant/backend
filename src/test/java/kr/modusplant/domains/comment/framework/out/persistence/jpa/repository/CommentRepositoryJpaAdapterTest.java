package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.common.util.usecase.CommentResponseTestUtils;
import kr.modusplant.domains.comment.common.util.domain.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper.CommentJpaMapper;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers.CommentJpaRepository;
import kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.CommCommentEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.CommCommentIdTestUtils;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import org.mockito.Mockito;

public class CommentRepositoryJpaAdapterTest implements PostIdTestUtils,
        CommentResponseTestUtils, MemberIdTestUtils,
        AuthorTestUtils, CommentTestUtils,
        CommCommentEntityTestUtils, CommCommentIdTestUtils {
    private final SiteMemberJpaRepository memberRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final CommentJpaRepository commentRepository = Mockito.mock(CommentJpaRepository.class);
    private final CommPostJpaRepository postRepository = Mockito.mock(CommPostJpaRepository.class);
    private final CommentJpaMapper mapper = Mockito.mock(CommentJpaMapper.class);
    private final CommentRepositoryJpaAdapter repositoryJpaAdapter = new CommentRepositoryJpaAdapter(
            memberRepository, postRepository, commentRepository, mapper);

//    @Test
//    @DisplayName("유효한 댓글로 댓글 저장")
//    public void testSave_givenValidComment_willSaveComment() {
//        // given
//        CommCommentEntity testCommCommentEntity = createCommCommentEntityBuilder().build();
//
//        given(commentRepository.save(testCommCommentEntity)).willReturn(testCommCommentEntity);
//        given(mapper.toCommCommentEntity(testValidComment)).willReturn(testCommCommentEntity);
//
//        // when
//        repositoryJpaAdapter.save(testValidComment);
//
//        // then
//        Mockito.verify(commentRepository, times(1)).save(testCommCommentEntity);
//    }

//    @Test
//    @DisplayName("댓글 id로 댓글 삭제")
//    public void testDeleteById_givenValidCommentId_willDeleteComment() {
//        // given
//        doNothing().when(commentRepository).deleteById(testCommCommentId);
//
//        // when
//        repositoryJpaAdapter.deleteById(testCommCommentId);
//
//        // then
//        Mockito.verify(commentRepository, times(1)).deleteById(testCommCommentId);
//    }
}
