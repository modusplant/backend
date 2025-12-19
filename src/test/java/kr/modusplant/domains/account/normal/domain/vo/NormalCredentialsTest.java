package kr.modusplant.domains.account.normal.domain.vo;

import kr.modusplant.domains.account.normal.common.util.domain.vo.NormalCredentialsTestUtils;
import kr.modusplant.shared.exception.EmptyEmailException;
import kr.modusplant.shared.exception.InvalidEmailException;
import kr.modusplant.shared.exception.InvalidPasswordException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NormalCredentialsTest implements NormalCredentialsTestUtils {

    @Test
    @DisplayName("null 값으로 자격 요소 생성")
    public void testCreate_givenNullEmailAndPassword_willThrowEmptyEmailException() {
        // given
        EmptyEmailException result = assertThrows(EmptyEmailException.class, () ->
                NormalCredentials.createWithString(null, null));

        // when & then
        assertEquals(ErrorCode.EMAIL_EMPTY, result.getErrorCode());
    }

    @Test
    @DisplayName("형식에 맞지 않는 이메일로 자격 요소 생성")
    public void testCreate_givenInvalidEmail_willThrowInvalidEmailException() {
        // given
        InvalidEmailException result = assertThrows(InvalidEmailException.class, () ->
                NormalCredentials.createWithString("email", testNormalCredentials.getPassword().getValue()));

        // when & then
        assertEquals(ErrorCode.INVALID_EMAIL, result.getErrorCode());
    }

    @Test
    @DisplayName("형식에 맞지 않는 비밀번호로 자격 요소 생성")
    public void testCreate_givenInvalidPassword_willThrowInvalidValueException() {
        // given
        InvalidPasswordException result = assertThrows(InvalidPasswordException.class, () ->
                NormalCredentials.createWithString(testNormalCredentials.getEmail().getValue(), "282933"));

        // when & then
        assertEquals(ErrorCode.INVALID_PASSWORD, result.getErrorCode());
    }

    @Test
    @DisplayName("동일한 객체로 동등성 비교")
    void testEquals_givenSameObject_willReturnTrue() {
        // given
        NormalCredentials same = testNormalCredentials;

        // when & then
        assertEquals(testNormalCredentials, testNormalCredentials);
    }

    @Test
    @DisplayName("다른 객체로 동등성 비교")
    void testEquals_givenDifferentObject_willReturnFalse() {
        RuntimeException different = new RuntimeException();
        assertNotEquals(testNormalCredentials, different);
    }

    @Test
    @DisplayName("동일하고 다른 프로퍼티를 지닌 객체로 동등성 비교")
    void testEquals_givenDifferentProperty_willReturnFalse() {
        // given
        NormalCredentials credentials = NormalCredentials.createWithString("jeho123@email.com", "myPassword123!");

        assertNotEquals(testNormalCredentials, credentials);
    }
}
