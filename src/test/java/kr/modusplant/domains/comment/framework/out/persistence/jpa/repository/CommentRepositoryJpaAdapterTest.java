package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.common.util.adapter.CommentResponseTestUtils;
import kr.modusplant.domains.comment.common.util.domain.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper.CommentJpaMapper;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers.CommentJpaRepository;
import kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.framework.out.jpa.entity.CommCommentEntity;
import kr.modusplant.framework.out.jpa.entity.common.util.CommCommentEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.CommCommentIdTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

public class CommentRepositoryJpaAdapterTest implements PostIdTestUtils,
        CommentResponseTestUtils, MemberIdTestUtils,
        AuthorTestUtils, CommentTestUtils,
        CommCommentEntityTestUtils, CommCommentIdTestUtils {
    private final CommentJpaRepository repository = Mockito.mock(CommentJpaRepository.class);
    private final CommentJpaMapper mapper = Mockito.mock(CommentJpaMapper.class);
    private final CommentRepositoryJpaAdapter repositoryJpaAdapter = new CommentRepositoryJpaAdapter(repository, mapper);

    @Test
    @DisplayName("유효한 댓글로 댓글 저장")
    public void testSave_givenValidComment_willSaveComment() {
        // given
        CommCommentEntity testCommCommentEntity = createCommCommentEntityBuilder().build();

        given(repository.save(testCommCommentEntity)).willReturn(testCommCommentEntity);
        given(mapper.toCommCommentEntity(testValidComment)).willReturn(testCommCommentEntity);

        // when
        repositoryJpaAdapter.save(testValidComment);

        // then
        Mockito.verify(repository, times(1)).save(testCommCommentEntity);
    }

    @Test
    @DisplayName("댓글 id로 댓글 삭제")
    public void testDeleteById_givenValidCommentId_willDeleteComment() {
        // given
        doNothing().when(repository).deleteById(testCommCommentId);

        // when
        repositoryJpaAdapter.deleteById(testCommCommentId);

        // then
        Mockito.verify(repository, times(1)).deleteById(testCommCommentId);
    }
}
