package kr.modusplant.domains.comment.adapter.helper;

import kr.modusplant.domains.comment.common.util.domain.vo.CommentAuthorIdTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.ImmediateParentPostIdTestUtils;
import kr.modusplant.domains.comment.domain.exception.DeletedReplyToCommentException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.usecase.port.repository.CommentAuthorRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentQueryRepository;
import kr.modusplant.domains.comment.usecase.port.repository.ImmediateParentPostRepository;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.exception.NotFoundValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

public class CommentValidationHelperTest implements ImmediateParentPostIdTestUtils, CommentPathTestUtils, CommentAuthorIdTestUtils {
    private final CommentQueryRepository queryRepository = Mockito.mock(CommentQueryRepository.class);
    private final CommentAuthorRepository authorRepository = Mockito.mock(CommentAuthorRepository.class);
    private final ImmediateParentPostRepository immediateParentPostRepository = Mockito.mock(ImmediateParentPostRepository.class);
    private final CommentValidationHelper helper = new CommentValidationHelper(
            queryRepository, authorRepository, immediateParentPostRepository);

    @Test
    @DisplayName("존재하는 작성자 검증 시 활동 수행")
    void testValidateIfAuthorExists_givenExistingAuthor_willProcessAction() {
        // given
        given(authorRepository.isExists(testCommentAuthorId)).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> helper.validateIfAuthorExists(testCommentAuthorId));
        then(authorRepository).should(times(1)).isExists(testCommentAuthorId);
    }

    @Test
    @DisplayName("존재하지 않는 작성자 검증 시 예외 반환")
    void testValidateIfAuthorExists_givenNonExistentAuthor_willThrowException() {
        // given
        given(authorRepository.isExists(testCommentAuthorId)).willReturn(false);

        // when
        NotFoundValueException ex = assertThrows(NotFoundValueException.class,
                () -> helper.validateIfAuthorExists(testCommentAuthorId));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_FOUND_COMMENT_AUTHOR);
    }

    @Test
    @DisplayName("존재하는 게시글 검증 시 활동 수행")
    void testValidateIfImmediateParentPostExists_givenExistingPost_willProcessAction() {
        // given
        given(immediateParentPostRepository.isImmediateParentPostExists(testImmediateParentPostId)).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> helper.validateIfImmediateParentPostExists(testImmediateParentPostId));
        then(immediateParentPostRepository).should(times(1)).isImmediateParentPostExists(testImmediateParentPostId);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 검증 시 예외 반환")
    void testValidateIfImmediateParentPostExists_givenNonExistentPost_willThrowException() {
        // given
        given(immediateParentPostRepository.isImmediateParentPostExists(testImmediateParentPostId)).willReturn(false);

        // when
        NotFoundValueException ex = assertThrows(NotFoundValueException.class,
                () -> helper.validateIfImmediateParentPostExists(testImmediateParentPostId));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_FOUND_IMMEDIATE_PARENT_POST);
    }

    @Test
    @DisplayName("게시된 게시글 검증 시 활동 수행")
    void testValidateIfImmediateParentPostIsPublished_givenPublishedPost_willProcessAction() {
        // given
        given(immediateParentPostRepository.isImmediateParentPostPublished(testImmediateParentPostId)).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> helper.validateIfImmediateParentPostIsPublished(testImmediateParentPostId));
        then(immediateParentPostRepository).should(times(1)).isImmediateParentPostPublished(testImmediateParentPostId);
    }

    @Test
    @DisplayName("게시되지 않은 게시글 검증 시 예외 반환")
    void testValidateIfImmediateParentPostIsPublished_givenUnpublishedPost_willThrowException() {
        // given
        given(immediateParentPostRepository.isImmediateParentPostPublished(testImmediateParentPostId)).willReturn(false);

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> helper.validateIfImmediateParentPostIsPublished(testImmediateParentPostId));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_PUBLISHED_POST);
    }

    @Test
    @DisplayName("존재하는 댓글 검증 시 활동 수행")
    void testValidateIfCommentExists_givenExistingComment_willProcessAction() {
        // given
        given(queryRepository.isCommentExists(testImmediateParentPostId, testCommentPath)).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> helper.validateIfCommentExists(testImmediateParentPostId, testCommentPath));
        then(queryRepository).should(times(1)).isCommentExists(testImmediateParentPostId, testCommentPath);
    }

    @Test
    @DisplayName("존재하지 않는 댓글 검증 시 예외 반환")
    void testValidateIfCommentExists_givenNonExistentComment_willThrowException() {
        // given
        given(queryRepository.isCommentExists(testImmediateParentPostId, testCommentPath)).willReturn(false);

        // when
        NotFoundValueException ex = assertThrows(NotFoundValueException.class,
                () -> helper.validateIfCommentExists(testImmediateParentPostId, testCommentPath));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_COMMENT);
    }

    @Test
    @DisplayName("삭제되지 않은 댓글 검증 시 활동 수행")
    void testValidateIfCommentIsNotDeleted_givenNotDeletedComment_willProcessAction() {
        // given
        given(queryRepository.isCommentDeleted(testImmediateParentPostId, testCommentPath)).willReturn(false);

        // when & then
        assertDoesNotThrow(() -> helper.validateIfCommentIsNotDeleted(testImmediateParentPostId, testCommentPath));
        then(queryRepository).should(times(1)).isCommentDeleted(testImmediateParentPostId, testCommentPath);
    }

    @Test
    @DisplayName("삭제된 댓글 검증 시 예외 반환")
    void testValidateIfCommentIsNotDeleted_givenDeletedComment_willThrowException() {
        // given
        given(queryRepository.isCommentDeleted(testImmediateParentPostId, testCommentPath)).willReturn(true);

        // when
        DeletedReplyToCommentException ex = assertThrows(DeletedReplyToCommentException.class,
                () -> helper.validateIfCommentIsNotDeleted(testImmediateParentPostId, testCommentPath));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.DELETED_REPLY_TO_COMMENT);
    }

    @Test
    @DisplayName("작성자 본인이 작성한 댓글 검증 시 활동 수행")
    void testValidateIfCommentWrittenByAuthor_givenCommentWrittenByAuthor_willProcessAction() {
        // given
        given(queryRepository.isCommentWrittenByAuthor(
                testImmediateParentPostId, testCommentPath, testCommentAuthorId)).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> helper.validateIfCommentWrittenByAuthor(testImmediateParentPostId, testCommentPath, testCommentAuthorId));
        then(queryRepository).should(times(1)).isCommentWrittenByAuthor(
                testImmediateParentPostId, testCommentPath, testCommentAuthorId);
    }

    @Test
    @DisplayName("작성자 본인이 작성하지 않은 댓글 검증 시 예외 반환")
    void testValidateIfCommentWrittenByAuthor_givenCommentNotWrittenByAuthor_willThrowException() {
        // given
        given(queryRepository.isCommentWrittenByAuthor(
                testImmediateParentPostId, testCommentPath, testCommentAuthorId)).willReturn(false);

        // when
        NotAccessibleException ex = assertThrows(NotAccessibleException.class,
                () -> helper.validateIfCommentWrittenByAuthor(testImmediateParentPostId, testCommentPath, testCommentAuthorId));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_WRITTEN_COMMENT_BY_AUTHOR);
    }

    @Test
    @DisplayName("작성자가 게시글에 댓글을 쓸 수 있는 모든 조건 충족 시 활동 수행")
    void testValidateIfAuthorCanWriteWithinPost_givenAllConditionsMet_willProcessAction() {
        // given
        given(authorRepository.isExists(testCommentAuthorId)).willReturn(true);
        given(immediateParentPostRepository.isImmediateParentPostExists(testImmediateParentPostId)).willReturn(true);
        given(immediateParentPostRepository.isImmediateParentPostPublished(testImmediateParentPostId)).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> helper.validateIfAuthorCanWriteWithinPost(testImmediateParentPostId, testCommentAuthorId));
        then(authorRepository).should(times(1)).isExists(testCommentAuthorId);
        then(immediateParentPostRepository).should(times(1)).isImmediateParentPostExists(testImmediateParentPostId);
        then(immediateParentPostRepository).should(times(1)).isImmediateParentPostPublished(testImmediateParentPostId);
    }

    @Test
    @DisplayName("작성자가 게시글에 댓글을 쓸 때 게시글이 게시되지 않았으면 예외 반환")
    void testValidateIfAuthorCanWriteWithinPost_givenUnpublishedPost_willThrowException() {
        // given
        given(authorRepository.isExists(testCommentAuthorId)).willReturn(true);
        given(immediateParentPostRepository.isImmediateParentPostExists(testImmediateParentPostId)).willReturn(true);
        given(immediateParentPostRepository.isImmediateParentPostPublished(testImmediateParentPostId)).willReturn(false);

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> helper.validateIfAuthorCanWriteWithinPost(testImmediateParentPostId, testCommentAuthorId));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_PUBLISHED_POST);
    }

    @Test
    @DisplayName("작성자가 기존 댓글을 수정/삭제할 수 있는 모든 조건 충족 시 활동 수행")
    void testValidateIfAuthorCanWriteCommentWithinPost_givenAllConditionsMet_willProcessAction() {
        // given
        given(authorRepository.isExists(testCommentAuthorId)).willReturn(true);
        given(immediateParentPostRepository.isImmediateParentPostExists(testImmediateParentPostId)).willReturn(true);
        given(immediateParentPostRepository.isImmediateParentPostPublished(testImmediateParentPostId)).willReturn(true);
        given(queryRepository.isCommentExists(testImmediateParentPostId, testCommentPath)).willReturn(true);
        given(queryRepository.isCommentWrittenByAuthor(
                testImmediateParentPostId, testCommentPath, testCommentAuthorId)).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> helper.validateIfAuthorCanWriteCommentWithinPost(testImmediateParentPostId, testCommentPath, testCommentAuthorId));
        then(queryRepository).should(times(1)).isCommentExists(testImmediateParentPostId, testCommentPath);
        then(queryRepository).should(times(1)).isCommentWrittenByAuthor(
                testImmediateParentPostId, testCommentPath, testCommentAuthorId);
    }

    @Test
    @DisplayName("작성자가 기존 댓글을 수정/삭제할 때 본인이 작성한 댓글이 아니면 예외 반환")
    void testValidateIfAuthorCanWriteCommentWithinPost_givenCommentNotWrittenByAuthor_willThrowException() {
        // given
        given(authorRepository.isExists(testCommentAuthorId)).willReturn(true);
        given(immediateParentPostRepository.isImmediateParentPostExists(testImmediateParentPostId)).willReturn(true);
        given(immediateParentPostRepository.isImmediateParentPostPublished(testImmediateParentPostId)).willReturn(true);
        given(queryRepository.isCommentExists(testImmediateParentPostId, testCommentPath)).willReturn(true);
        given(queryRepository.isCommentWrittenByAuthor(
                testImmediateParentPostId, testCommentPath, testCommentAuthorId)).willReturn(false);

        // when
        NotAccessibleException ex = assertThrows(NotAccessibleException.class,
                () -> helper.validateIfAuthorCanWriteCommentWithinPost(testImmediateParentPostId, testCommentPath, testCommentAuthorId));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_WRITTEN_COMMENT_BY_AUTHOR);
    }
}
