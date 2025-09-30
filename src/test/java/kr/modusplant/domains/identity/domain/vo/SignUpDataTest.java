package kr.modusplant.domains.identity.domain.vo;

import kr.modusplant.domains.identity.common.util.domain.vo.AgreedTermsOfVersionTestUtils;
import kr.modusplant.domains.identity.common.util.domain.vo.CredentialsTestUtils;
import kr.modusplant.domains.identity.common.util.domain.vo.NicknameTestUtils;
import kr.modusplant.domains.identity.common.util.domain.vo.SignUpDataTestUtils;
import kr.modusplant.domains.identity.domain.exception.EmptyValueException;
import kr.modusplant.domains.identity.domain.exception.InvalidValueException;
import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SignUpDataTest implements SignUpDataTestUtils, CredentialsTestUtils,
        NicknameTestUtils, AgreedTermsOfVersionTestUtils {

    @Test
    @DisplayName("null 값으로 회원가입 정보 생성")
    public void testCreate_givenNullEmail_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () ->
                SignUpData.create(null, testCredentials.getPassword(),
                        testNickname.getNickname(), testAgreedTermsOfVersion.getVersion(),
                        testAgreedTermsOfVersion.getVersion(), testAgreedTermsOfVersion.getVersion()));

        // when & then
        assertEquals(IdentityErrorCode.EMPTY_EMAIL, result.getErrorCode());
    }

    @Test
    @DisplayName("형식에 맞지 않는 값으로 회원가입 정보 생성")
    public void testCreate_givenInvalidEmail_willThrowInvalidValueException() {
        // given
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                SignUpData.create("testCredentials.getEmail()", testCredentials.getPassword(),
                        testNickname.getNickname(), testAgreedTermsOfVersion.getVersion(),
                        testAgreedTermsOfVersion.getVersion(), testAgreedTermsOfVersion.getVersion()));

        // when & then
        assertEquals(IdentityErrorCode.INVALID_EMAIL, result.getErrorCode());
    }

    @Test
    @DisplayName("동일한 객체로 동등성 비교")
    void testEquals_givenSameObject_willReturnTrue() {
        // given
        SignUpData sign = testSignUpData;

        // when & then
        assertEquals(sign, sign);
    }

    @Test
    @DisplayName("다른 객체로 동등성 비교")
    void testEquals_givenDifferentObject_willReturnFalse() {
        EmptyValueException different = new EmptyValueException(IdentityErrorCode.EMPTY_NICKNAME);
        assertNotEquals(testSignUpData, different);
    }

    @Test
    @DisplayName("동일하고 다른 프로퍼티를 지닌 객체로 동등성 비교")
    void testEquals_givenDifferentProperty_willReturnFalse() {
        // given
        SignUpData signUpData = SignUpData.create("fame@example.com", testCredentials.getPassword(),
                testNickname.getNickname(), testAgreedTermsOfVersion.getVersion(),
                testAgreedTermsOfVersion.getVersion(), testAgreedTermsOfVersion.getVersion());

        assertNotEquals(testNickname, signUpData);
    }
}
