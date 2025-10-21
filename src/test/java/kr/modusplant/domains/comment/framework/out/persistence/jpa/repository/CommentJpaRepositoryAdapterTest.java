package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.common.util.adapter.CommentResponseTestUtils;
import kr.modusplant.domains.comment.common.util.domain.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.common.util.framework.CommentCompositeKeyTestUtils;
import kr.modusplant.domains.comment.common.util.framework.CommentEntityTestUtils;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper.CommentJpaMapper;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers.CommentJpaRepository;
import kr.modusplant.domains.comment.usecase.response.CommentResponse;
import kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

public class CommentJpaRepositoryAdapterTest implements PostIdTestUtils,
        CommentResponseTestUtils, MemberIdTestUtils,
        AuthorTestUtils, CommentTestUtils,
        CommentEntityTestUtils, CommentCompositeKeyTestUtils {
    private final CommentJpaRepository repository = Mockito.mock(CommentJpaRepository.class);
    private final CommentJpaMapper mapper = Mockito.mock(CommentJpaMapper.class);
    private final CommentRepositoryJpaAdapter repositoryJpaAdapter = new CommentRepositoryJpaAdapter(repository, mapper);

    @Test
    @DisplayName("유효한 게시글 id로 댓글 가져오기")
    public void testFindByPost_givenValidPostId_willReturnCommentResponseList() {
        // given
        given(repository.findByPostUlid(testPostId.getId())).willReturn(List.of(testCommentResponse));

        // when
        List<CommentResponse> result = repositoryJpaAdapter.findByPost(testPostId);

        // then
        assertThat(result).isEqualTo(List.of(testCommentResponse));
    }

    @Test
    @DisplayName("유효한 작성자 id로 댓글 가져오기")
    public void testFindByAuthor_givenValidAuthor_willReturnCommentResponseList() {
        // given
        given(repository.findByAuthMemberUuid(testAuthor.getMemberUuid())).willReturn(List.of(testCommentResponse));

        // when
        List<CommentResponse> result = repositoryJpaAdapter.findByAuthor(testAuthorWithUuid);

        // then
        assertThat(result).isEqualTo(List.of(testCommentResponse));
    }

    @Test
    @DisplayName("유효한 댓글로 댓글 저장")
    public void testSave_givenValidComment_willSaveComment() {
        // given
        CommentEntity testCommentEntity = createCommentEntity();

        given(repository.save(testCommentEntity)).willReturn(testCommentEntity);
        given(mapper.toCommentEntity(testValidComment)).willReturn(testCommentEntity);

        // when
        repositoryJpaAdapter.save(testValidComment);

        // then
        Mockito.verify(repository, times(1)).save(testCommentEntity);
    }

    @Test
    @DisplayName("댓글 id로 댓글 삭제")
    public void testDeleteById_givenValidCommentId_willDeleteComment() {
        // given
        doNothing().when(repository).deleteById(testCommentCompositeKey);

        // when
        repositoryJpaAdapter.deleteById(testCommentCompositeKey);

        // then
        Mockito.verify(repository, times(1)).deleteById(testCommentCompositeKey);
    }
}
