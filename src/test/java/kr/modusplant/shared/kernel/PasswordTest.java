package kr.modusplant.shared.kernel;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.kernel.common.util.PasswordTestUtils;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordTest implements PasswordTestUtils {

    @Test
    @DisplayName("null로 비밀번호 생성 시 예외 반환")
    public void testCreate_givenNull_willThrowException() {
        // given & when
        EmptyValueException result = assertThrows(EmptyValueException.class, () -> Password.create(null));

        // then
        assertEquals(KernelErrorCode.EMPTY_PASSWORD, result.getErrorCode());
    }

    @Test
    @DisplayName("형식에 맞지 않는 값으로 비밀번호 생성 시 예외 반환")
    public void testCreate_givenInvalidFormat_willThrowException() {
        // given & when
        InvalidValueException result = assertThrows(InvalidValueException.class,
                () -> Password.create("a".repeat(7)));

        // then
        assertEquals(KernelErrorCode.INVALID_PASSWORD_FORMAT, result.getErrorCode());
    }

    @Test
    @DisplayName("같은 객체로 참 반환")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testNormalUserPassword, testNormalUserPassword);
    }

    @Test
    @DisplayName("다른 객체로 거짓 반환")
    void testEquals_givenDifferentObject_willReturnFalse() {
        EmptyValueException different = new EmptyValueException(KernelErrorCode.EMPTY_PASSWORD, "password");
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testNormalUserPassword, different);
    }

    @Test
    @DisplayName("다른 프로퍼티 객체로 거짓 반환")
    void testEquals_givenDifferentProperty_willReturnFalse() {
        // given
        Password different = Password.create(MEMBER_AUTH_BASIC_USER_PW + "1");

        // when & then
        assertNotEquals(testNormalUserPassword, different);
    }

    @Test
    @DisplayName("같은 객체로 같은 해시코드 반환")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testNormalUserPassword.hashCode(), testNormalUserPassword.hashCode());
    }
}
