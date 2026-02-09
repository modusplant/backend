package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberBirthDateTestUtils.testMemberBirthDate;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberIdTest implements MemberTestUtils {
    @Test
    @DisplayName("generate으로 회원 ID 반환")
    void testGenerate_givenNoParameter_willReturnMemberId() {
        assertNotNull(MemberId.generate().getValue());
    }

    @Test
    @DisplayName("fromUuid로 회원 ID 반환")
    void testFromUuid_givenValidValue_willReturnMemberId() {
        assertNotNull(MemberId.fromUuid(UUID.randomUUID()).getValue());
    }

    @Test
    @DisplayName("null로 fromUuid를 호출하여 오류 발생")
    void testFromUuid_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> MemberId.fromUuid(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_ID);
    }

    @Test
    @DisplayName("fromString으로 회원 ID 반환")
    void testFromString_givenValidValue_willReturnMemberId() {
        assertNotNull(MemberId.fromString(UUID.randomUUID().toString()).getValue());
    }

    @Test
    @DisplayName("null로 fromString을 호출하여 오류 발생")
    void testFromString_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> MemberId.fromString(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_ID);
    }

    @Test
    @DisplayName("빈 문자열로 fromString을 호출하여 오류 발생")
    void testFromString_givenEmptyString_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> MemberId.fromString("   "));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_ID);
    }

    @Test
    @DisplayName("정규 표현식에 매칭되지 않는 값으로 fromString을 호출하여 오류 발생")
    void testFromString_givenInvalidId_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> MemberId.fromString("!유효하지않음!"));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT);
        assertThat(exception.getValueName()).isEqualTo("memberId");
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberId, testMemberId);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberId, testMemberBirthDate);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testMemberId, MemberId.generate());
    }
}