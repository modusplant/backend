package kr.modusplant.domains.identity.normal.domain.vo;

import kr.modusplant.domains.identity.normal.common.util.domain.vo.NicknameTestUtils;
import kr.modusplant.domains.identity.normal.domain.exception.EmptyValueException;
import kr.modusplant.domains.identity.normal.domain.exception.InvalidValueException;
import kr.modusplant.domains.identity.normal.domain.exception.enums.IdentityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NicknameTest implements NicknameTestUtils {

    @Test
    @DisplayName("null 값으로 닉네임 생성")
    public void testCreate_givenNullNickname_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () ->
                Nickname.create(null));

        // when & then
        assertEquals(IdentityErrorCode.EMPTY_NICKNAME, result.getErrorCode());
    }

    @Test
    @DisplayName("형식에 맞지 않는 값으로 닉네임 생성")
    public void testCreate_givenInvalidEmail_willThrowInvalidValueException() {
        // given
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                Nickname.create("nickname!!!!!"));

        // when & then
        assertEquals(IdentityErrorCode.INVALID_NICKNAME, result.getErrorCode());
    }

    @Test
    @DisplayName("동일한 객체로 동등성 비교")
    void testEquals_givenSameObject_willReturnTrue() {
        // given
        Nickname nickname = testNickname;

        // when & then
        assertEquals(nickname, nickname);
    }

    @Test
    @DisplayName("다른 객체로 동등성 비교")
    void testEquals_givenDifferentObject_willReturnFalse() {
        EmptyValueException different = new EmptyValueException(IdentityErrorCode.EMPTY_NICKNAME);
        assertNotEquals(testNickname, different);
    }

    @Test
    @DisplayName("동일하고 다른 프로퍼티를 지닌 객체로 동등성 비교")
    void testEquals_givenDifferentProperty_willReturnFalse() {
        // given
        Nickname nickname = Nickname.create("nickname");

        assertNotEquals(testNickname, nickname);
    }
}
