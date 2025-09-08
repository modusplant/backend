package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.support.utils.domain.CommentPathTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommentPathTest implements CommentPathTestUtils {

    @Test
    @DisplayName("빈 문자열로 댓글 경로를 생성할 시 커스텀 예외를 던짐")
    public void callCreate_whenBlankPath_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () ->
                CommentPath.create(""));

        // when & then
        assertEquals(CommentErrorCode.EMPTY_COMMENT_PATH, result.getErrorCode());
    }

    @Test
    @DisplayName("빈 문자열로 댓글 경로를 생성할 시 커스텀 예외를 던짐")
    public void callCreate_whenInvalidPath_willThrowInvalidValueException() {
        // given
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                CommentPath.create("3/d.0"));

        // when & then
        assertEquals(CommentErrorCode.INVALID_COMMENT_PATH, result.getErrorCode());
    }
}
