package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.common.util.domain.vo.MemberBirthDateTestUtils;
import kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.domains.member.domain.exception.EmptyMemberBirthDateException;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberBirthDateTest implements MemberBirthDateTestUtils, MemberIdTestUtils {
    @Test
    @DisplayName("create로 회원 생일 반환")
    void testCreate_givenValidValue_willReturnMemberBirthDate() {
        // given
        LocalDate now = LocalDate.now();

        // when & then
        assertThat(MemberBirthDate.create(now)).isEqualTo(MemberBirthDate.create(now));
    }

    @Test
    @DisplayName("null로 create를 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyMemberBirthDateException exception = assertThrows(EmptyMemberBirthDateException.class, () -> MemberBirthDate.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_BIRTH_DATE);
    }


    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberBirthDate, testMemberBirthDate);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberBirthDate, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testMemberBirthDate, MemberBirthDate.create(LocalDate.MIN));
    }
}