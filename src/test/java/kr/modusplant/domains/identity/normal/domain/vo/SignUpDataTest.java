package kr.modusplant.domains.identity.normal.domain.vo;

import kr.modusplant.domains.identity.normal.common.util.domain.vo.AgreedTermsOfVersionTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.EmailTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.PasswordTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.SignUpDataTestUtils;
import kr.modusplant.domains.identity.normal.domain.exception.EmptyValueException;
import kr.modusplant.domains.identity.normal.domain.exception.enums.NormalIdentityErrorCode;
import kr.modusplant.shared.exception.EmptyEmailException;
import kr.modusplant.shared.exception.InvalidEmailException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SignUpDataTest implements SignUpDataTestUtils, EmailTestUtils, PasswordTestUtils,
        NicknameTestUtils, AgreedTermsOfVersionTestUtils {

    @Test
    @DisplayName("null 값으로 회원가입 정보 생성")
    public void testCreate_givenNullEmail_willThrowEmptyEmailException() {
        // given
        EmptyEmailException result = assertThrows(EmptyEmailException.class, () ->
                SignUpData.create(null, TEST_NORMAL_PASSWORD.getValue(),
                        testNormalUserNickname.getValue(), testAgreedTermsOfUse.getValue(),
                        testAgreedPrivacyPolicy.getValue(), testAgreedAdReceiving.getValue()));

        // when & then
        assertEquals(ErrorCode.EMAIL_EMPTY, result.getErrorCode());
    }

    @Test
    @DisplayName("형식에 맞지 않는 값으로 회원가입 정보 생성")
    public void testCreate_givenInvalidEmail_willThrowInvalidEmailException() {
        // given
        InvalidEmailException result = assertThrows(InvalidEmailException.class, () ->
                SignUpData.create("testCredentials.getEmail()", TEST_NORMAL_PASSWORD.getValue(),
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
        EmptyValueException different = new EmptyValueException(NormalIdentityErrorCode.EMPTY_NICKNAME);
        assertNotEquals(TEST_NORMAL_SIGN_UP_DATA, different);
    }

    @Test
    @DisplayName("동일하고 다른 프로퍼티를 지닌 객체로 동등성 비교")
    void testEquals_givenDifferentProperty_willReturnFalse() {
        // given
        SignUpData signUpData = SignUpData.create(testEmail.getValue(), TEST_NORMAL_PASSWORD.getValue(),
                testNormalUserNickname.getValue(), testAgreedPrivacyPolicy.getValue(),
                testAgreedTermsOfUse.getValue(), testAgreedAdReceiving.getValue());

        assertNotEquals(testNormalUserNickname, signUpData);
    }
}
