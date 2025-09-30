package kr.modusplant.domains.comment.domain.aggregate;

import kr.modusplant.domains.comment.common.util.domain.*;
import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CommentTest implements
        CommentTestUtils, PostIdTestUtils, CommentPathTestUtils, AuthorTestUtils,
        CommentContentTestUtils, CommentStatusTestUtils {

    @Test
    @DisplayName("null인 게시글 id로 댓글 생성")
    void testCreate_givenNullPostId_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(
                EmptyValueException.class, () -> Comment.create(null, testCommentPath,
                        testAuthor, testCommentContent)
        );

        // when & then
        assertThat(result.getErrorCode()).isEqualTo(CommentErrorCode.EMPTY_POST_ID);
    }

    @Test
    @DisplayName("null인 댓글 경로로 댓글 생성")
    void testCreate_givenNullCommentPath_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(
                EmptyValueException.class, () -> Comment.create(testPostId, null,
                        testAuthor, testCommentContent)
        );

        // when & then
        assertThat(result.getErrorCode()).isEqualTo(CommentErrorCode.INVALID_COMMENT_PATH);
    }

    @Test
    @DisplayName("null인 작성자로 댓글 생성")
    void testCreate_givenNullAuthor_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(
                EmptyValueException.class, () -> Comment.create(testPostId, testCommentPath,
                        null, testCommentContent)
        );

        // when & then
        assertThat(result.getErrorCode()).isEqualTo(CommentErrorCode.EMPTY_AUTHOR);
    }

    @Test
    @DisplayName("null인 댓글 내용으로 댓글 생성")
    void testCreate_givenNullContent_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(
                EmptyValueException.class, () -> Comment.create(testPostId, testCommentPath,
                        testAuthor, null)
        );

        // when & then
        assertThat(result.getErrorCode()).isEqualTo(CommentErrorCode.EMPTY_COMMENT_CONTENT);
    }

    @Test
    @DisplayName("null인 댓글 상태로 댓글 생성")
    void testCreate_givenNullStatus_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(
                EmptyValueException.class, () -> Comment.create(testPostId, testCommentPath,
                        testAuthor, testCommentContent, null)
        );

        // when & then
        assertThat(result.getErrorCode()).isEqualTo(CommentErrorCode.EMPTY_COMMENT_STATUS);
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
        assertNotEquals(testValidComment, testCommentPath);
    }

    @Test
    @DisplayName("동일하고 다른 프로퍼티를 지닌 객체로 동등성 비교")
    void testEquals_givenDifferentProperty_willReturnFalse() {
        // given
        Comment compare = Comment.create(testPostId, CommentPath.create("1.1.1.1"), testAuthor, testCommentContent);

        assertNotEquals(testValidComment, compare);
    }
}
