package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.support.utils.domain.AuthorTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthorTest implements AuthorTestUtils {

    @Test
    @DisplayName("create() 메서드의 MemberUuid 가 null 인 경우 EmptyValueException 호출")
    public void callCreate_whenInvalidMemberUuid_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () -> Author.create(null));

        // when & then
        assertEquals(CommentErrorCode.EMPTY_AUTHOR, result.getErrorCode());
    }

    @Test
    @DisplayName("create() 메서드의 MemberNickname 이 null 인 경우 EmptyValueException 호출")
    public void callCreate_whenInvalidMemberNickname_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () -> Author.create(testAuthor.getMemberUuid(), null));

        // when & then
        assertEquals(CommentErrorCode.EMPTY_MEMBER_NICKNAME, result.getErrorCode());
    }
}
