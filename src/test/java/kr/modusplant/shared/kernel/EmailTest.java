package kr.modusplant.shared.kernel;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_KAKAO_USER_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EmailTest implements EmailTestUtils {

    @Test
    @DisplayName("Email 문자열로 Email 반환")
    void testCreate_givenValidEmailString_willReturnEmail() {
        // given
        String emailValue = MEMBER_AUTH_KAKAO_USER_EMAIL;

        // when
        Email email = Email.create(emailValue);

        // then
        assertThat(email.getValue()).isEqualTo(emailValue);
    }

    @Test
    @DisplayName("null이나 빈 문자열로 이메일 생성 시 예외 반환")
    void testCreate_givenEmptyEmail_willThrowException() {
        // given & when & then
        EmptyValueException nullException = assertThrows(EmptyValueException.class, () -> Email.create(null));
        EmptyValueException emptyException = assertThrows(EmptyValueException.class, () -> Email.create(""));
        EmptyValueException blankException = assertThrows(EmptyValueException.class, () -> Email.create("   "));
        assertThat(nullException.getErrorCode()).isEqualTo(KernelErrorCode.EMPTY_EMAIL);
        assertThat(emptyException.getErrorCode()).isEqualTo(KernelErrorCode.EMPTY_EMAIL);
        assertThat(blankException.getErrorCode()).isEqualTo(KernelErrorCode.EMPTY_EMAIL);
    }

    @Test
    @DisplayName("유효하지 않은 문자열로 이메일 생성 시 예외 반환")
    void testCreate_givenInvalidEmailFormat_willThrowException() {
        // given & when & then
        InvalidValueException noLocalPartException = assertThrows(InvalidValueException.class, () -> Email.create("invalid-email"));
        InvalidValueException emptyLocalPartException = assertThrows(InvalidValueException.class, () -> Email.create("@example.com"));
        assertThat(noLocalPartException.getErrorCode()).isEqualTo(KernelErrorCode.INVALID_EMAIL_FORMAT);
        assertThat(emptyLocalPartException.getErrorCode()).isEqualTo(KernelErrorCode.INVALID_EMAIL_FORMAT);
    }

    @Test
    @DisplayName("같은 객체로 참 반환")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testKakaoUserEmail, testKakaoUserEmail);
    }

    @Test
    @DisplayName("다른 클래스 인스턴스로 거짓 반환")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testKakaoUserEmail, "Different Class");
    }

    @Test
    @DisplayName("다른 프로퍼티 인스턴스로 거짓 반환")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testKakaoUserEmail, testGoogleUserEmail);
    }

    @Test
    @DisplayName("같은 객체로 같은 해시코드 반환")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testKakaoUserEmail.hashCode(), testKakaoUserEmail.hashCode());
    }
}
