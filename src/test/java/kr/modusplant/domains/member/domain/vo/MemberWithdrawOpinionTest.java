package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.constant.MemberWithdrawConstant.MEMBER_WITHDRAW_BASIC_USER_OPINION;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberStatusTestUtils.testMemberActiveStatus;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberWithdrawOpinionTestUtils.testMemberWithdrawOpinion;
import static kr.modusplant.domains.member.common.util.domain.vo.nullobject.EmptyMemberWithdrawOpinionTestUtils.testEmptyMemberWithdrawOpinion;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.MEMBER_WITHDRAW_OPINION_OVER_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberWithdrawOpinionTest {
    @Test
    @DisplayName("create으로 회원 탈퇴 관련 의견 반환")
    void testCreate_givenValidValue_willReturnMemberWithdrawOpinion() {
        assertNotNull(MemberWithdrawOpinion.create(MEMBER_WITHDRAW_BASIC_USER_OPINION).getValue());
    }

    @Test
    @DisplayName("null로 create을 호출하여 비어 있는 회원 탈퇴 관련 의견 반환")
    void testCreate_givenNull_willReturnEmptyMemberWithdrawOpinion() {
        assertThat(MemberWithdrawOpinion.create(null)).isEqualTo(testEmptyMemberWithdrawOpinion);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 비어 있는 회원 탈퇴 관련 의견 반환")
    void testCreate_willThrowException() {
        assertThat(MemberWithdrawOpinion.create(" ")).isEqualTo(testEmptyMemberWithdrawOpinion);
    }

    @Test
    @DisplayName("600자 이상의 값으로 create을 호출하여 오류 발생")
    void testCreate_givenTooLongString_willThrowException() {
        // given & when
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> MemberWithdrawOpinion.create("a".repeat(601)));
        
        // then
        assertThat(exception.getErrorCode()).isEqualTo(MEMBER_WITHDRAW_OPINION_OVER_LENGTH);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberWithdrawOpinion, testMemberWithdrawOpinion);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberWithdrawOpinion, testMemberActiveStatus);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberWithdrawOpinion, "1".repeat(16));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testMemberWithdrawOpinion.hashCode(), testMemberWithdrawOpinion.hashCode());
    }
}