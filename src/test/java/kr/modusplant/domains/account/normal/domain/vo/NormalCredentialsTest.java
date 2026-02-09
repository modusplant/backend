package kr.modusplant.domains.account.normal.domain.vo;

import kr.modusplant.domains.account.normal.common.util.domain.vo.NormalCredentialsTestUtils;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NormalCredentialsTest implements NormalCredentialsTestUtils {

    @Test
    @DisplayName("null 값으로 자격 요소 생성")
    public void testCreate_givenNullEmailAndPassword_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () ->
                NormalCredentials.createWithString(null, null));

        // when & then
        assertEquals(KernelErrorCode.EMPTY_EMAIL, result.getErrorCode());
    }

    @Test
    @DisplayName("형식에 맞지 않는 이메일로 자격 요소 생성")
    public void testCreate_givenInvalidEmail_willThrowInvalidValueException() {
        // given
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                NormalCredentials.createWithString("email", testNormalCredentials.getPassword().getValue()));

        // when & then
        assertEquals(KernelErrorCode.INVALID_EMAIL_FORMAT, result.getErrorCode());
    }

    @Test
    @DisplayName("형식에 맞지 않는 비밀번호로 자격 요소 생성")
    public void testCreate_givenInvalidPassword_willThrowInvalidValueException() {
        // given
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                NormalCredentials.createWithString(testNormalCredentials.getEmail().getValue(), "282933"));

        // when & then
        assertEquals(KernelErrorCode.INVALID_PASSWORD_FORMAT, result.getErrorCode());
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
