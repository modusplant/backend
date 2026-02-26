package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileIntroductionTestUtils.testMemberProfileIntroduction;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_INTRODUCTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberProfileIntroductionTest {
    @Test
    @DisplayName("create으로 회원 프로필 소개 반환")
    void testCreate_givenValidValue_willReturnMemberProfileIntroduction() {
        assertThat(MemberProfileIntroduction.create(MEMBER_PROFILE_BASIC_USER_INTRODUCTION)).isEqualTo(MemberProfileIntroduction.create(MEMBER_PROFILE_BASIC_USER_INTRODUCTION));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> MemberProfileIntroduction.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_PROFILE_INTRODUCTION);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenEmptyString_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> MemberProfileIntroduction.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_PROFILE_INTRODUCTION);
    }

    @Test
    @DisplayName("60자를 초과하는 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenStringExceeding60Chars_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> MemberProfileIntroduction.create("a".repeat(61)));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.MEMBER_PROFILE_INTRODUCTION_OVER_LENGTH);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberProfileIntroduction, testMemberProfileIntroduction);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberProfileIntroduction, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testMemberProfileIntroduction, MemberProfileIntroduction.create(String.valueOf(UUID.randomUUID())));
    }
}