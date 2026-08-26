package kr.modusplant.domains.comment.framework.outbound.jpa.repository;

import kr.modusplant.domains.comment.common.util.domain.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.common.util.framework.outbound.jpa.compositekey.CommentCompositeKeyTestUtils;
import kr.modusplant.domains.comment.common.util.framework.outbound.jpa.entity.CommentEntityTestUtils;
import kr.modusplant.domains.comment.common.util.usecase.response.CommentResponseTestUtils;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.framework.outbound.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.outbound.jpa.mapper.CommentJpaMapper;
import kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.persistence.constant.TableName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CommentRepositoryJpaAdapterTest implements PostIdTestUtils,
        CommentResponseTestUtils, MemberIdTestUtils,
        AuthorTestUtils, CommentTestUtils, CommentPathTestUtils,
        CommentEntityTestUtils, CommentCompositeKeyTestUtils {
    private final MemberJpaRepository memberRepository = Mockito.mock(MemberJpaRepository.class);
    private final CommentJpaRepository commentRepository = Mockito.mock(CommentJpaRepository.class);
    private final PostJpaRepository postRepository = Mockito.mock(PostJpaRepository.class);
    private final CommentJpaMapper mapper = Mockito.mock(CommentJpaMapper.class);
    private final CommentRepositoryJpaAdapter jpaAdapter = new CommentRepositoryJpaAdapter(
            memberRepository, postRepository, commentRepository, mapper);

    @Test
    @DisplayName("유효한 댓글로 댓글 저장")
    public void testSave_givenValidComment_willSaveComment() {
        // given
        MemberEntity memberEntity = Mockito.mock(MemberEntity.class);
        PostEntity postEntity = Mockito.mock(PostEntity.class);
        CommentEntity commentEntity = createCommentEntityBuilder().build();

        given(memberRepository.findByUuid(testValidComment.getAuthor().getUuid())).willReturn(Optional.of(memberEntity));
        given(postRepository.findByUlid(testValidComment.getPostId().getValue())).willReturn(Optional.of(postEntity));
        given(mapper.toCommCommentEntity(testValidComment, memberEntity, postEntity)).willReturn(commentEntity);
        given(commentRepository.existsById(TEST_COMMENT_ID)).willReturn(false);

        // when
        jpaAdapter.save(testValidComment);

        // then
        verify(commentRepository, times(1)).save(commentEntity);
    }

    @Test
    @DisplayName("존재하지 않는 작성자로 댓글 저장 시 InvalidValueException 발생")
    public void testSave_givenNonExistentAuthor_willThrowInvalidValueException() {
        // given
        given(memberRepository.findByUuid(testValidComment.getAuthor().getUuid())).willReturn(Optional.empty());

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> jpaAdapter.save(testValidComment));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_EXIST_AUTHOR);
    }

    @Test
    @DisplayName("존재하지 않는 게시글로 댓글 저장 시 InvalidValueException 발생")
    public void testSave_givenNonExistentPost_willThrowInvalidValueException() {
        // given
        MemberEntity memberEntity = Mockito.mock(MemberEntity.class);
        given(memberRepository.findByUuid(testValidComment.getAuthor().getUuid())).willReturn(Optional.of(memberEntity));
        given(postRepository.findByUlid(testValidComment.getPostId().getValue())).willReturn(Optional.empty());

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> jpaAdapter.save(testValidComment));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_EXIST_POST);
    }

    @Test
    @DisplayName("이미 존재하는 댓글 경로로 저장 시 InvalidValueException 발생")
    public void testSave_givenExistingComment_willThrowInvalidValueException() {
        // given
        MemberEntity memberEntity = Mockito.mock(MemberEntity.class);
        PostEntity postEntity = Mockito.mock(PostEntity.class);
        CommentEntity commentEntity = createCommentEntityBuilder().build();

        given(memberRepository.findByUuid(testValidComment.getAuthor().getUuid())).willReturn(Optional.of(memberEntity));
        given(postRepository.findByUlid(testValidComment.getPostId().getValue())).willReturn(Optional.of(postEntity));
        given(mapper.toCommCommentEntity(testValidComment, memberEntity, postEntity)).willReturn(commentEntity);
        given(commentRepository.existsById(TEST_COMMENT_ID)).willReturn(true);

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> jpaAdapter.save(testValidComment));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.EXIST_COMMENT);
    }

    @Test
    @DisplayName("유효한 댓글 id와 내용으로 댓글 갱신")
    public void testUpdate_givenValidCommentIdAndContent_willSaveCommentWithNewContent() {
        // given
        CommentEntity beforeUpdate = createCommentEntityBuilder()
                .content("content before updating.")
                .build();
        CommentEntity afterUpdate = createCommentEntityBuilder().build();
        given(commentRepository.findById(TEST_COMMENT_ID)).willReturn(Optional.of(beforeUpdate));
        given(commentRepository.save(afterUpdate)).willReturn(null);

        // when
        jpaAdapter.update(PostIdTestUtils.testPostId, testCommentPath, CommentContent.create(afterUpdate.getContent()));

        // then
        verify(commentRepository, times(1)).findById(TEST_COMMENT_ID);
        verify(commentRepository, times(1)).save(afterUpdate);
    }

    @Test
    @DisplayName("무효한 댓글 id와 내용으로 댓글 갱신")
    public void testUpdate_givenInvalidCommentIdAndContent_willThrowError() {
        // given
        CommentEntity afterUpdate = createCommentEntityBuilder().build();
        given(commentRepository.findById(TEST_COMMENT_ID)).willReturn(Optional.empty());
        given(commentRepository.save(afterUpdate)).willReturn(null);

        // when
        NotFoundEntityException ex = assertThrows(
                NotFoundEntityException.class, () -> jpaAdapter.update(PostIdTestUtils.testPostId, testCommentPath, CommentContent.create(afterUpdate.getContent())));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_COMMENT);
        assertThat(ex.getEntityName()).isEqualTo(TableName.COMM_COMMENT);
    }

    @Test
    @DisplayName("존재하는 댓글 id로 삭제 처리 시 markAsDeleted 후 저장됨")
    public void testSetCommentAsDeleted_givenExistingComment_willMarkAsDeletedAndSave() {
        // given
        CommentEntity commentEntity = createCommentEntityBuilder().build();
        given(commentRepository.findById(TEST_COMMENT_ID)).willReturn(Optional.of(commentEntity));

        // when
        jpaAdapter.setCommentAsDeleted(PostIdTestUtils.testPostId, testCommentPath);

        // then
        assertThat(commentEntity.getIsDeleted()).isTrue();
        verify(commentRepository, times(1)).save(commentEntity);
    }

    @Test
    @DisplayName("존재하지 않는 댓글 id로 삭제 처리 시 아무 동작도 하지 않음")
    public void testSetCommentAsDeleted_givenNonExistentComment_willDoNothing() {
        // given
        given(commentRepository.findById(TEST_COMMENT_ID)).willReturn(Optional.empty());

        // when
        jpaAdapter.setCommentAsDeleted(PostIdTestUtils.testPostId, testCommentPath);

        // then
        verify(commentRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
