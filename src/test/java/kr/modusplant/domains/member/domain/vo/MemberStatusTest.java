package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.EmptyMemberStatusException;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.common.utils.domain.MemberIdTestUtils;
import kr.modusplant.domains.member.common.utils.domain.MemberStatusTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberStatusTest implements MemberStatusTestUtils, MemberIdTestUtils {
    @Test
    @DisplayName("active로 회원 상태 반환")
    void callActive_withNoParameter_returnsMemberStatus() {
        assertThat(MemberStatus.active()).isEqualTo(MemberStatus.active());
    }

    @Test
    @DisplayName("inactive로 회원 상태 반환")
    void callInactive_withNoParameter_returnsMemberStatus() {
        assertThat(MemberStatus.inactive()).isEqualTo(MemberStatus.inactive());
    }

    @Test
    @DisplayName("fromBoolean으로 회원 상태 반환")
    void callFromBoolean_withValidValue_returnsMemberStatus() {
        assertTrue(MemberStatus.fromBoolean(true).isActive());
        assertTrue(MemberStatus.fromBoolean(false).isInactive());
        assertFalse(MemberStatus.fromBoolean(true).isInactive());
        assertFalse(MemberStatus.fromBoolean(false).isActive());
    }

    @Test
    @DisplayName("null로 fromBoolean을 호출하여 오류 발생")
    void callFromBoolean_withNull_throwsException() {
        EmptyMemberStatusException exception = assertThrows(EmptyMemberStatusException.class, () -> MemberStatus.fromBoolean(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_STATUS);
    }

    @Test
    @DisplayName("isActive로 불리언 상태 반환")
    void callIsActive_withNoParameter_returnsBoolean() {
        assertTrue(testMemberActiveStatus.isActive());
        assertTrue(testMemberInactiveStatus.isInactive());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_withSameObject_returnsTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberActiveStatus, testMemberActiveStatus);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_withObjectOfDifferentClass_returnsFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberActiveStatus, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_withObjectContainingDifferentProperty_returnsFalse() {
        assertNotEquals(testMemberActiveStatus, testMemberInactiveStatus);
    }
}