package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.common.util.domain.vo.nullobject.EmptyMemberBirthDateTestUtils;
import kr.modusplant.domains.member.domain.vo.MemberBirthDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EmptyMemberBirthDateTest implements EmptyMemberBirthDateTestUtils {
    @Test
    @DisplayName("create로 비어 있는 회원 생일 반환")
    void testCreate_givenNothing_willReturnEmptyMemberBirthDate() {
        assertThat(EmptyMemberBirthDate.create()).isEqualTo(EmptyMemberBirthDate.create());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(TEST_EMPTY_MEMBER_BIRTH_DATE, TEST_EMPTY_MEMBER_BIRTH_DATE);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(TEST_EMPTY_MEMBER_BIRTH_DATE, testMemberId);
    }

    @Test
    @DisplayName("MemberBirthDate 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(TEST_EMPTY_MEMBER_BIRTH_DATE, MemberBirthDate.create(LocalDate.MIN));
    }
}