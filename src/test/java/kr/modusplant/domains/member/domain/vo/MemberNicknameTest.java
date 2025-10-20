package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.common.util.domain.vo.MemberNicknameTestUtils;
import kr.modusplant.domains.member.domain.exception.EmptyMemberNicknameException;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.InvalidDataException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_MEMBER_NICKNAME_STRING;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberNicknameTest implements MemberNicknameTestUtils {
    @Test
    @DisplayName("create으로 회원 닉네임 반환")
    void testCreate_givenValidValue_willReturnMemberNickname() {
        assertThat(MemberNickname.create(TEST_MEMBER_NICKNAME_STRING)).isEqualTo(MemberNickname.create(TEST_MEMBER_NICKNAME_STRING));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyMemberNicknameException exception = assertThrows(EmptyMemberNicknameException.class, () -> MemberNickname.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_NICKNAME);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenEmptyString_willThrowException() {
        EmptyMemberNicknameException exception = assertThrows(EmptyMemberNicknameException.class, () -> MemberNickname.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_NICKNAME);
    }

    @Test
    @DisplayName("정규 표현식에 매칭되지 않는 값으로 create을 호출하여 오류 발생")
    void testCreate_givenInvalidNickname_willThrowException() {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> MemberNickname.create("!유효하지않음!"));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT);
        assertThat(exception.getDataName()).isEqualTo("memberNickname");
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberNickname, testMemberNickname);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberNickname, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testMemberNickname, MemberNickname.create(TEST_MEMBER_NICKNAME_STRING + "1"));
    }
}