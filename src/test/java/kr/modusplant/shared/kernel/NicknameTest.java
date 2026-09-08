package kr.modusplant.shared.kernel;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.kernel.common.util.EmailTestUtils.testNormalUserEmail;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NicknameTest {
    @Test
    @DisplayName("유효한 값으로 Nickname 반환")
    void testCreate_givenValidValue_willReturnNickname() {
        assertThat(Nickname.create(MEMBER_BASIC_USER_NICKNAME)).isEqualTo(Nickname.create(MEMBER_BASIC_USER_NICKNAME));
    }

    @Test
    @DisplayName("null로 닉네임 생성 시 예외 반환")
    void testCreate_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> Nickname.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(KernelErrorCode.EMPTY_NICKNAME);
    }

    @Test
    @DisplayName("빈 문자열로 닉네임 생성 시 예외 반환")
    void testCreate_givenEmptyString_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> Nickname.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(KernelErrorCode.EMPTY_NICKNAME);
    }

    @Test
    @DisplayName("정규식에 맞지 않는 값으로 닉네임 생성 시 예외 반환")
    void testCreate_givenInvalidNickname_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> Nickname.create("!유효하지않음!"));
        assertThat(exception.getErrorCode()).isEqualTo(KernelErrorCode.INVALID_NICKNAME_FORMAT);
    }

    @Test
    @DisplayName("같은 객체로 참 반환")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testNormalUserNickname, testNormalUserNickname);
    }

    @Test
    @DisplayName("다른 클래스 인스턴스로 거짓 반환")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testNormalUserNickname, testNormalUserEmail);
    }

    @Test
    @DisplayName("다른 프로퍼티 인스턴스로 거짓 반환")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testNormalUserNickname, Nickname.create(MEMBER_BASIC_USER_NICKNAME + "1"));
    }

    @Test
    @DisplayName("같은 객체로 같은 해시코드 반환")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testNormalUserNickname.hashCode(), testNormalUserNickname.hashCode());
    }
}
