package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.common.util.domain.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.common.util.usecase.CommentResponseTestUtils;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper.CommentJpaMapper;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers.CommentJpaRepository;
import kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.framework.jpa.entity.CommCommentEntity;
import kr.modusplant.framework.jpa.entity.common.util.CommCommentEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.CommCommentIdTestUtils;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.shared.persistence.constant.TableName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CommentRepositoryJpaAdapterTest implements PostIdTestUtils,
        CommentResponseTestUtils, MemberIdTestUtils,
        AuthorTestUtils, CommentTestUtils,
        CommCommentEntityTestUtils, CommCommentIdTestUtils {
    private final SiteMemberJpaRepository memberRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final CommentJpaRepository commentRepository = Mockito.mock(CommentJpaRepository.class);
    private final CommPostJpaRepository postRepository = Mockito.mock(CommPostJpaRepository.class);
    private final CommentJpaMapper mapper = Mockito.mock(CommentJpaMapper.class);
    private final CommentRepositoryJpaAdapter jpaAdapter = new CommentRepositoryJpaAdapter(
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

    @Test
    @DisplayName("유효한 댓글 id와 내용으로 댓글 갱신")
    public void testUpdate_givenValidCommentIdAndContent_willSaveCommentWithNewContent() {
        // given
        CommCommentEntity beforeUpdate = createCommCommentEntityBuilder()
                .content("content before updating.")
                .build();
        CommCommentEntity afterUpdate = createCommCommentEntityBuilder().build();
        given(commentRepository.findById(testCommCommentId)).willReturn(Optional.of(beforeUpdate));
        given(commentRepository.save(afterUpdate)).willReturn(null);

        // when
        jpaAdapter.update(testCommCommentId, CommentContent.create(afterUpdate.getContent()));

        // then
        verify(commentRepository, times(1)).findById(testCommCommentId);
        verify(commentRepository, times(1)).save(afterUpdate);
    }

    @Test
    @DisplayName("무효한 댓글 id와 내용으로 댓글 갱신")
    public void testUpdate_givenInvalidCommentIdAndContent_willThrowError() {
        // given
        CommCommentEntity afterUpdate = createCommCommentEntityBuilder().build();
        given(commentRepository.findById(testCommCommentId)).willReturn(Optional.empty());
        given(commentRepository.save(afterUpdate)).willReturn(null);

        // when
        NotFoundEntityException ex = assertThrows(
                NotFoundEntityException.class, () -> jpaAdapter.update(testCommCommentId, CommentContent.create(afterUpdate.getContent())));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_COMMENT);
        assertThat(ex.getEntityName()).isEqualTo(TableName.COMM_COMMENT);
    }

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
