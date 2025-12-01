package kr.modusplant.domains.identity.normal.domain.vo;

import kr.modusplant.domains.identity.normal.common.util.domain.vo.EmailTestUtils;
import kr.modusplant.domains.identity.normal.domain.exception.EmptyValueException;
import kr.modusplant.domains.identity.normal.domain.exception.InvalidValueException;
import kr.modusplant.domains.identity.normal.domain.exception.enums.NormalIdentityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EmailTest implements EmailTestUtils {

    @Test
    @DisplayName("null로 이메일 생성")
    public void testCreate_givenNull_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () ->
                Email.create(null));

        // when & then
        assertEquals(NormalIdentityErrorCode.EMPTY_EMAIL, result.getErrorCode());
    }

    @Test
    @DisplayName("형식에 맞지 않는 값으로 이메일 생성")
    public void testCreate_givenInvalidFormat_willThrowInvalidValueException() {
        // given
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                Email.create("va11223"));

        // when & then
        assertEquals(NormalIdentityErrorCode.INVALID_EMAIL, result.getErrorCode());
    }

    @Test
    @DisplayName("동일한 객체로 동등성 비교")
    void testEquals_givenSameObject_willReturnTrue() {
        // given
        Email email = testEmail;

        // when & then
        assertEquals(email, testEmail);
    }

    @Test
    @DisplayName("다른 객체로 동등성 비교")
    void testEquals_givenDifferentObject_willReturnFalse() {
        // given & when & then
        EmptyValueException different = new EmptyValueException(NormalIdentityErrorCode.EMPTY_NICKNAME);
        assertNotEquals(testEmail, different);
    }

    @Test
    @DisplayName("동일하고 다른 프로퍼티를 지닌 객체로 동등성 비교")
    void testEquals_givenDifferentProperty_willReturnFalse() {
        // given
        Email different = Email.create("plant12@example.com");

        // when & then
        assertNotEquals(testEmail, different);
    }

}
