package kr.modusplant.shared.kernel;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.kernel.common.util.PasswordTestUtils;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordTest implements PasswordTestUtils {

    @Test
    @DisplayName("null로 비밀번호 생성")
    public void testCreate_givenNull_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () -> Password.create(null));

        // when & then
        assertEquals(KernelErrorCode.EMPTY_PASSWORD, result.getErrorCode());
    }

    @Test
    @DisplayName("형식에 맞지 않는 값으로 비밀번호 생성")
    public void testCreate_givenInvalidFormat_willThrowInvalidValueException() {
        // given
        InvalidValueException result = assertThrows(InvalidValueException.class,
                () -> Password.create("a".repeat(7)));

        // when & then
        assertEquals(KernelErrorCode.INVALID_PASSWORD_FORMAT, result.getErrorCode());
    }

    @Test
    @DisplayName("동일한 객체로 동등성 비교")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testNormalUserPassword, testNormalUserPassword);
    }

    @Test
    @DisplayName("다른 객체로 동등성 비교")
    void testEquals_givenDifferentObject_willReturnFalse() {
        EmptyValueException different = new EmptyValueException(KernelErrorCode.EMPTY_PASSWORD, "password");
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testNormalUserPassword, different);
    }

    @Test
    @DisplayName("동일하고 다른 프로퍼티를 지닌 객체로 동등성 비교")
    void testEquals_givenDifferentProperty_willReturnFalse() {
        // given
        Password different = Password.create(MEMBER_AUTH_BASIC_USER_PW + "1");

        // when & then
        assertNotEquals(testNormalUserPassword, different);
    }
}
