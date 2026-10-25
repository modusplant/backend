package kr.modusplant.domains.comment.domain.aggregate;

import kr.modusplant.domains.comment.common.util.domain.vo.*;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.nullobject.EmptyReplyToCommentPath;
import kr.modusplant.shared.exception.EmptyValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CommentTest implements
        CommentTestUtils, PostIdTestUtils, CommentPathTestUtils, ReplyToCommentPathTestUtils, AuthorTestUtils,
        CommentContentTestUtils, CommentStatusTestUtils {

    @Test
    @DisplayName("null인 게시글 id로 댓글 생성 시 예외 반환")
    void testCreate_givenNullPostId_willThrowException() {
        // given
        EmptyValueException result = assertThrows(
                EmptyValueException.class, () -> Comment.create(null, testCommentPath, testReplyToCommentPath,
                        testCommentAuthor, testCommentContent)
        );

        // when & then
        assertThat(result.getErrorCode()).isEqualTo(CommentErrorCode.EMPTY_POST_ID);
    }

    @Test
    @DisplayName("null인 댓글 경로로 댓글 생성 시 예외 반환")
    void testCreate_givenNullCommentPath_willThrowException() {
        // given
        EmptyValueException result = assertThrows(
                EmptyValueException.class, () -> Comment.create(testPostId, null, testReplyToCommentPath,
                        testCommentAuthor, testCommentContent)
        );

        // when & then
        assertThat(result.getErrorCode()).isEqualTo(CommentErrorCode.EMPTY_COMMENT_PATH);
    }

    @Test
    @DisplayName("null인 답글 대상 경로로 댓글 생성 시 예외 반환")
    void testCreate_givenNullReplyToPath_willThrowException() {
        // given
        EmptyValueException result = assertThrows(
                EmptyValueException.class, () -> Comment.create(testPostId, testCommentPath, null,
                        testCommentAuthor, testCommentContent)
        );

        // when & then
        assertThat(result.getErrorCode()).isEqualTo(CommentErrorCode.EMPTY_REPLY_TO_COMMENT_PATH);
    }

    @Test
    @DisplayName("상태를 지정하며 null인 답글 대상 경로로 댓글 생성 시 예외 반환")
    void testCreateWithStatus_givenNullReplyToPath_willThrowException() {
        // given
        EmptyValueException result = assertThrows(
                EmptyValueException.class, () -> Comment.create(testPostId, testCommentPath, null,
                        testCommentAuthor, testCommentContent, testCommentStatusValid)
        );

        // when & then
        assertThat(result.getErrorCode()).isEqualTo(CommentErrorCode.EMPTY_REPLY_TO_COMMENT_PATH);
    }

    @Test
    @DisplayName("null인 작성자로 댓글 생성 시 예외 반환")
    void testCreate_givenNullAuthor_willThrowException() {
        // given
        EmptyValueException result = assertThrows(
                EmptyValueException.class, () -> Comment.create(testPostId, testCommentPath, testReplyToCommentPath,
                        null, testCommentContent)
        );

        // when & then
        assertThat(result.getErrorCode()).isEqualTo(CommentErrorCode.EMPTY_AUTHOR);
    }

    @Test
    @DisplayName("null인 댓글 내용으로 댓글 생성 시 예외 반환")
    void testCreate_givenNullContent_willThrowException() {
        // given
        EmptyValueException result = assertThrows(
                EmptyValueException.class, () -> Comment.create(testPostId, testCommentPath, testReplyToCommentPath,
                        testCommentAuthor, null)
        );

        // when & then
        assertThat(result.getErrorCode()).isEqualTo(CommentErrorCode.EMPTY_COMMENT_CONTENT);
    }

    @Test
    @DisplayName("null인 댓글 상태로 댓글 생성 시 예외 반환")
    void testCreate_givenNullStatus_willThrowException() {
        // given
        EmptyValueException result = assertThrows(
                EmptyValueException.class, () -> Comment.create(testPostId, testCommentPath, testReplyToCommentPath,
                        testCommentAuthor, testCommentContent, null)
        );

        // when & then
        assertThat(result.getErrorCode()).isEqualTo(CommentErrorCode.EMPTY_COMMENT_STATUS);
    }

    @Test
    @DisplayName("비어 있는 답글 대상 경로로 댓글 생성 시 Comment 반환")
    void testCreate_givenEmptyReplyToPath_willReturnComment() {
        // when
        Comment result = Comment.create(testPostId, testCommentPath, EmptyReplyToCommentPath.create(), testCommentAuthor, testCommentContent);

        // then
        assertThat(result.getReplyToPath()).isSameAs(EmptyReplyToCommentPath.create());
        assertThat(result.getPath()).isEqualTo(testCommentPath);
    }

    @Test
    @DisplayName("동일한 객체로 동등성 비교")
    void testEquals_givenSameObject_willReturnTrue() {
        // given
        Comment comment = testValidComment;

        // when & then
        assertEquals(comment, comment);
    }

    @Test
    @DisplayName("다른 객체로 동등성 비교")
    void testEquals_givenDifferentObject_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testValidComment, testCommentPath);
    }

    @Test
    @DisplayName("동일하고 다른 프로퍼티를 지닌 객체로 동등성 비교")
    void testEquals_givenDifferentProperty_willReturnFalse() {
        // given
        Comment compare = Comment.create(testPostId, CommentPath.create("1"), EmptyReplyToCommentPath.create(), testCommentAuthor, testCommentContent);

        assertNotEquals(testValidComment, compare);
    }
}
