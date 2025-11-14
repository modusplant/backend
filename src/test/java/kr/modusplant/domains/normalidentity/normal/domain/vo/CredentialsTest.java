package kr.modusplant.domains.normalidentity.normal.domain.vo;

import kr.modusplant.domains.identity.normal.domain.vo.Credentials;
import kr.modusplant.domains.normalidentity.normal.common.util.domain.vo.CredentialsTestUtils;
import kr.modusplant.domains.identity.normal.domain.exception.EmptyValueException;
import kr.modusplant.domains.identity.normal.domain.exception.InvalidValueException;
import kr.modusplant.domains.identity.normal.domain.exception.enums.IdentityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CredentialsTest implements CredentialsTestUtils {

    @Test
    @DisplayName("null 값으로 자격 요소 생성")
    public void testCreate_givenNullEmailAndPassword_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () ->
                Credentials.createWithString(null, null));

        // when & then
        assertEquals(IdentityErrorCode.EMPTY_EMAIL, result.getErrorCode());
    }

    @Test
    @DisplayName("형식에 맞지 않는 이메일로 자격 요소 생성")
    public void testCreate_givenInvalidEmail_willThrowInvalidValueException() {
        // given
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                Credentials.createWithString("email", testCredentials.getPassword().getPassword()));

        // when & then
        assertEquals(IdentityErrorCode.INVALID_EMAIL, result.getErrorCode());
    }

    @Test
    @DisplayName("형식에 맞지 않는 비밀번호로 자격 요소 생성")
    public void testCreate_givenInvalidPassword_willThrowInvalidValueException() {
        // given
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                Credentials.createWithString(testCredentials.getEmail().getEmail(), "282933"));

        // when & then
        assertEquals(IdentityErrorCode.INVALID_PASSWORD, result.getErrorCode());
    }

    @Test
    @DisplayName("동일한 객체로 동등성 비교")
    void testEquals_givenSameObject_willReturnTrue() {
        // given
        Credentials same = testCredentials;

        // when & then
        assertEquals(testCredentials, testCredentials);
    }

    @Test
    @DisplayName("다른 객체로 동등성 비교")
    void testEquals_givenDifferentObject_willReturnFalse() {
        EmptyValueException different = new EmptyValueException(IdentityErrorCode.EMPTY_NICKNAME);
        assertNotEquals(testCredentials, different);
    }

    @Test
    @DisplayName("동일하고 다른 프로퍼티를 지닌 객체로 동등성 비교")
    void testEquals_givenDifferentProperty_willReturnFalse() {
        // given
        Credentials credentials = Credentials.createWithString("jeho123@email.com", "myPassword123!");

        assertNotEquals(testCredentials, credentials);
    }
}
