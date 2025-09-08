package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.support.utils.domain.CommentContentTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommentContentTest implements CommentContentTestUtils {

    @Test
    @DisplayName("빈 String으로 생성하려 시도할 시 EmptyValueException 호출")
    public void callCreate_whenBlankContent_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () ->
                CommentContent.create(""));

        // when & then
        assertEquals(CommentErrorCode.EMPTY_COMMENT_CONTENT, result.getErrorCode());
    }
}
