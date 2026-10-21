package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.common.util.domain.vo.ReplyToCommentPathTestUtils;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.nullobject.EmptyReplyToCommentPath;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_REPLY_TO_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ReplyToCommentPathTest implements ReplyToCommentPathTestUtils {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("비어 있는 값으로 답글 대상 댓글 경로 생성 시 EmptyReplyToCommentPath 반환")
    void testCreate_givenBlankValue_willReturnEmptyReplyToCommentPath(String value) {
        // when
        ReplyToCommentPath result = ReplyToCommentPath.create(value);

        // then
        assertThat(result).isSameAs(EmptyReplyToCommentPath.create());
    }

    @Test
    @DisplayName("형식이 맞지 않는 값으로 답글 대상 댓글 경로 생성 시 예외 반환")
    void testCreate_givenInvalidFormat_willThrowException() {
        // when
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                ReplyToCommentPath.create("3/d.0"));

        // then
        assertEquals(CommentErrorCode.INVALID_COMMENT_PATH_FORMAT, result.getErrorCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.1", "1.0", "1.01"})
    @DisplayName("0으로 시작하는 인덱스를 포함한 값으로 답글 대상 댓글 경로 생성 시 예외 반환")
    void testCreate_givenZeroLeadingIndex_willThrowException(String value) {
        // when
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                ReplyToCommentPath.create(value));

        // then
        assertEquals(CommentErrorCode.INVALID_COMMENT_PATH_INDEX, result.getErrorCode());
    }

    @Test
    @DisplayName("최대 깊이를 초과하는 값으로 답글 대상 댓글 경로 생성 시 예외 반환")
    void testCreate_givenValueDeeperThanMaxDepth_willThrowException() {
        // when
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                ReplyToCommentPath.create("1.2.3"));

        // then
        assertEquals(CommentErrorCode.INVALID_COMMENT_PATH_DEPTH, result.getErrorCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "1.1", "12.34"})
    @DisplayName("유효한 값으로 답글 대상 댓글 경로 생성 시 ReplyToCommentPath 반환")
    void testCreate_givenValidValue_willReturnReplyToCommentPath(String value) {
        // when
        ReplyToCommentPath result = ReplyToCommentPath.create(value);

        // then
        assertThat(result).isNotInstanceOf(EmptyReplyToCommentPath.class);
        assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testReplyToCommentPath, testReplyToCommentPath);
    }

    @Test
    @DisplayName("같은 값을 지닌 객체에 대한 equals 호출")
    void testEquals_givenObjectWithSameValue_willReturnTrue() {
        assertEquals(testReplyToCommentPath, ReplyToCommentPath.create(TEST_COMMENT_REPLY_TO_PATH));
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testReplyToCommentPath, CommentPath.create(TEST_COMMENT_REPLY_TO_PATH));
    }

    @Test
    @DisplayName("다른 값을 지닌 객체에 대한 equals 호출")
    void testEquals_givenObjectWithDifferentValue_willReturnFalse() {
        assertNotEquals(testReplyToCommentPath, ReplyToCommentPath.create("2.1"));
    }

    @Test
    @DisplayName("같은 값을 지닌 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenObjectWithSameValue_willReturnSameHashCode() {
        assertEquals(testReplyToCommentPath.hashCode(), ReplyToCommentPath.create(TEST_COMMENT_REPLY_TO_PATH).hashCode());
    }
}
