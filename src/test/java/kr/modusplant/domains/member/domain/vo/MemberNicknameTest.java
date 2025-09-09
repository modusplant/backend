package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.common.utils.domain.vo.MemberNicknameTestUtils;
import kr.modusplant.domains.member.domain.exception.EmptyMemberNicknameException;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_MEMBER_NICKNAME;
import static kr.modusplant.domains.member.common.utils.domain.vo.MemberIdTestUtils.testMemberId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberNicknameTest implements MemberNicknameTestUtils {
    @Test
    @DisplayName("create으로 회원 닉네임 반환")
    void callCreate_withValidValue_returnsMemberNickname() {
        assertThat(MemberNickname.create(TEST_MEMBER_NICKNAME)).isEqualTo(MemberNickname.create(TEST_MEMBER_NICKNAME));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void callCreate_withNull_throwsException() {
        EmptyMemberNicknameException exception = assertThrows(EmptyMemberNicknameException.class, () -> MemberNickname.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_NICKNAME);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_withSameObject_returnsTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberNickname, testMemberNickname);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_withObjectOfDifferentClass_returnsFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberNickname, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_withObjectContainingDifferentProperty_returnsFalse() {
        assertNotEquals(testMemberNickname, MemberNickname.create(TEST_MEMBER_NICKNAME + "1"));
    }
}