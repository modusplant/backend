package kr.modusplant.domains.account.normal.domain.vo;

import kr.modusplant.domains.account.normal.common.util.domain.vo.AgreedTermVersionTestUtils;
import kr.modusplant.domains.account.normal.common.util.domain.vo.SignUpDataTestUtils;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidEmailException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;
import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;
import kr.modusplant.shared.kernel.common.util.PasswordTestUtils;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SignUpDataTest implements SignUpDataTestUtils, EmailTestUtils, PasswordTestUtils,
        NicknameTestUtils, AgreedTermVersionTestUtils {

    @Test
    @DisplayName("null 값으로 회원가입 정보 생성")
    public void testCreate_givenNullEmail_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () ->
                SignUpData.create(null, testNormalUserPassword.getValue(),
                        testNormalUserNickname.getValue(), testAgreedTermsOfUse.getValue(),
                        testAgreedPrivacyPolicy.getValue(), testAgreedAdReceiving.getValue()));

        // when & then
        assertEquals(KernelErrorCode.EMPTY_EMAIL, result.getErrorCode());
    }

    @Test
    @DisplayName("형식에 맞지 않는 값으로 회원가입 정보 생성")
    public void testCreate_givenInvalidEmail_willThrowInvalidEmailException() {
        // given
        InvalidEmailException result = assertThrows(InvalidEmailException.class, () ->
                SignUpData.create("testCredentials.getEmail()", testNormalUserPassword.getValue(),
                        testNormalUserNickname.getValue(), testAgreedTermsOfUse.getValue(),
                        testAgreedPrivacyPolicy.getValue(), testAgreedAdReceiving.getValue()));

        // when & then
        assertEquals(ErrorCode.INVALID_EMAIL, result.getErrorCode());
    }

    @Test
    @DisplayName("동일한 객체로 동등성 비교")
    void testEquals_givenSameObject_willReturnTrue() {
        // given
        SignUpData sign = TEST_NORMAL_SIGN_UP_DATA;

        // when & then
        assertEquals(sign, sign);
    }

    @Test
    @DisplayName("다른 객체로 동등성 비교")
    void testEquals_givenDifferentObject_willReturnFalse() {
        RuntimeException different = new RuntimeException();
        assertNotEquals(TEST_NORMAL_SIGN_UP_DATA, different);
    }

    @Test
    @DisplayName("동일하고 다른 프로퍼티를 지닌 객체로 동등성 비교")
    void testEquals_givenDifferentProperty_willReturnFalse() {
        // given
        SignUpData signUpData = SignUpData.create(testNormalUserEmail.getValue(), testNormalUserPassword.getValue(),
                testNormalUserNickname.getValue(), testAgreedPrivacyPolicy.getValue(),
                testAgreedTermsOfUse.getValue(), testAgreedAdReceiving.getValue());

        assertNotEquals(testNormalUserNickname, signUpData);
    }
}
