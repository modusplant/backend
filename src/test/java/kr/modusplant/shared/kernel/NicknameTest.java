package kr.modusplant.shared.kernel;

import kr.modusplant.shared.exception.EmptyNicknameException;
import kr.modusplant.shared.exception.InvalidNicknameException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.kernel.common.util.EmailTestUtils.testNormalUserEmail;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NicknameTest {
    @Test
    @DisplayName("create으로 회원 닉네임 반환")
    void testCreate_givenValidValue_willReturnMemberNickname() {
        assertThat(Nickname.create(MEMBER_BASIC_USER_NICKNAME)).isEqualTo(Nickname.create(MEMBER_BASIC_USER_NICKNAME));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyNicknameException exception = assertThrows(EmptyNicknameException.class, () -> Nickname.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NICKNAME_EMPTY);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenEmptyString_willThrowException() {
        EmptyNicknameException exception = assertThrows(EmptyNicknameException.class, () -> Nickname.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NICKNAME_EMPTY);
    }

    @Test
    @DisplayName("정규 표현식에 매칭되지 않는 값으로 create을 호출하여 오류 발생")
    void testCreate_givenInvalidNickname_willThrowException() {
        InvalidNicknameException exception = assertThrows(InvalidNicknameException.class, () -> Nickname.create("!유효하지않음!"));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_NICKNAME);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testNormalUserNickname, testNormalUserNickname);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testNormalUserNickname, testNormalUserEmail);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testNormalUserNickname, Nickname.create(MEMBER_BASIC_USER_NICKNAME + "1"));
    }
}