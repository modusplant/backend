package kr.modusplant.shared.kernel;

import kr.modusplant.shared.exception.EmptyEmailException;
import kr.modusplant.shared.exception.InvalidEmailException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_KAKAO_USER_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EmailTest implements EmailTestUtils {

    @Test
    @DisplayName("Email 문자열로 Email 생성하기")
    void testCreate_givenValidEmailString_willReturnEmailVo() {
        assertNotNull(testKakaoUserEmail);
        assertThat(testKakaoUserEmail.getValue()).isEqualTo(MEMBER_AUTH_KAKAO_USER_EMAIL);
    }

    @Test
    @DisplayName("null이나 빈 문자열으로 이메일 생성시 예외 발생")
    void testCreate_givenEmptyEmail_willThrowException() {
        // when & then
        EmptyEmailException exception1 = assertThrows(EmptyEmailException.class, () -> Email.create(null));
        EmptyEmailException exception2 = assertThrows(EmptyEmailException.class, () -> Email.create(""));
        EmptyEmailException exception3 = assertThrows(EmptyEmailException.class, () -> Email.create("   "));
        assertThat(exception1.getErrorCode()).isEqualTo(ErrorCode.EMAIL_EMPTY);
        assertThat(exception2.getErrorCode()).isEqualTo(ErrorCode.EMAIL_EMPTY);
        assertThat(exception3.getErrorCode()).isEqualTo(ErrorCode.EMAIL_EMPTY);
    }

    @Test
    @DisplayName("유효하지 않은 문자열로 이메일 생성 시 예외 발생")
    void testCreate_givenInvalidEmailFormat_willThrowException() {
        InvalidEmailException exception1 = assertThrows(InvalidEmailException.class, () -> Email.create("invalid-email"));
        InvalidEmailException exception2 = assertThrows(InvalidEmailException.class, () -> Email.create("@example.com"));
        assertThat(exception1.getErrorCode()).isEqualTo(ErrorCode.INVALID_EMAIL);
        assertThat(exception2.getErrorCode()).isEqualTo(ErrorCode.INVALID_EMAIL);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testKakaoUserEmail, testKakaoUserEmail);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testKakaoUserEmail, "Different Class");
    }

    @Test
    @DisplayName("다른 프로퍼티를 가진 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testKakaoUserEmail, testGoogleUserEmail);
    }
}