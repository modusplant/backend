package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.common.util.domain.CommentStatusTestUtils;
import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommentStatusTest implements CommentStatusTestUtils {

    @Test
    @DisplayName("빈 값으로 댓글 상태 생성")
    public void testCreate_givenBlankPath_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () ->
                CommentStatus.create(""));

        // when & then
        assertEquals(CommentErrorCode.EMPTY_COMMENT_STATUS, result.getErrorCode());
    }

    @Test
    @DisplayName("잘못된 형식으로 댓글 상태 생성")
    public void testCreate_givenInvalidPath_willThrowInvalidValueException() {
        // given
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                CommentStatus.create("3/d.0"));

        // when & then
        assertEquals(CommentErrorCode.INVALID_COMMENT_STATUS, result.getErrorCode());
    }
}
