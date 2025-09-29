package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.support.utils.domain.AuthorTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthorTest implements AuthorTestUtils {

    @Test
    @DisplayName("null인 사용자의 id로 작성자 생성")
    public void testCreate_givenInvalidMemberUuid_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () -> Author.create(null));

        // when & then
        assertEquals(CommentErrorCode.EMPTY_AUTHOR, result.getErrorCode());
    }

    @Test
    @DisplayName("null인 작성자의 닉네임으로 작성자 생성")
    public void testCreate_givenInvalidMemberNickname_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () -> Author.create(testAuthor.getMemberUuid(), null));

        // when & then
        assertEquals(CommentErrorCode.EMPTY_MEMBER_NICKNAME, result.getErrorCode());
    }
}
