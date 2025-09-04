package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.EmptyMemberIdException;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.common.utils.domain.MemberTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberIdTest implements MemberTestUtils {
    @Test
    @DisplayName("generate으로 회원 ID 반환")
    void callGenerate_withNoParameter_returnsMemberId() {
        assertNotNull(MemberId.generate().getValue());
    }

    @Test
    @DisplayName("fromUuid로 회원 ID 반환")
    void callFromUuid_withValidValue_returnsMemberId() {
        assertNotNull(MemberId.fromUuid(UUID.randomUUID()).getValue());
    }

    @Test
    @DisplayName("null로 fromUuid를 호출하여 오류 발생")
    void callFromUuid_withNull_throwsException() {
        EmptyMemberIdException exception = assertThrows(EmptyMemberIdException.class, () -> MemberId.fromUuid(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_ID);
    }

    @Test
    @DisplayName("fromString으로 회원 ID 반환")
    void callFromString_withValidValue_returnsMemberId() {
        assertNotNull(MemberId.fromString(UUID.randomUUID().toString()).getValue());
    }

    @Test
    @DisplayName("null로 fromString을 호출하여 오류 발생")
    void callFromString_withNull_throwsException() {
        EmptyMemberIdException exception = assertThrows(EmptyMemberIdException.class, () -> MemberId.fromString(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_ID);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_withSameObject_returnsTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberId, testMemberId);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_withObjectOfDifferentClass_returnsFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberId, testMemberBirthDate);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_withObjectContainingDifferentProperty_returnsFalse() {
        assertNotEquals(testMemberId, MemberId.generate());
    }
}