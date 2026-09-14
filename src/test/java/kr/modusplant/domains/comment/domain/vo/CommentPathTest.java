package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.common.util.domain.CommentPathTestUtils;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommentPathTest implements CommentPathTestUtils {

    @Test
    @DisplayName("빈 문자열로 댓글 경로 생성")
    public void testCreate_givenBlankPath_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () ->
                CommentPath.create(""));

        // when & then
        assertEquals(CommentErrorCode.EMPTY_COMMENT_PATH, result.getErrorCode());
    }

    @Test
    @DisplayName("형식이 맞지 않는 댓글 경로 생성")
    public void testCreate_givenInvalidPath_willThrowInvalidValueException() {
        // given
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                CommentPath.create("3/d.0"));

        // when & then
        assertEquals(CommentErrorCode.INVALID_COMMENT_PATH_FORMAT, result.getErrorCode());
    }

    @Test
    @DisplayName("최대 깊이를 초과하는 댓글 경로 생성")
    public void testCreate_givenPathDeeperThanMaxDepth_willThrowInvalidValueException() {
        // given
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                CommentPath.create("1.2.3"));

        // when & then
        assertEquals(CommentErrorCode.INVALID_COMMENT_PATH_DEPTH, result.getErrorCode());
    }
}
