package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.common.utils.domain.vo.MemberBirthDateTestUtils;
import kr.modusplant.domains.member.common.utils.domain.vo.MemberIdTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MemberBirthDateTest implements MemberBirthDateTestUtils, MemberIdTestUtils {
    @Test
    @DisplayName("create으로 회원 생일 반환")
    void callCreate_withValidValue_returnsMemberBirthDate() {
        // given
        LocalDate now = LocalDate.now();

        // when & then
        assertThat(MemberBirthDate.create(now)).isEqualTo(MemberBirthDate.create(now));
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_withSameObject_returnsTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberBirthDate, testMemberBirthDate);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_withObjectOfDifferentClass_returnsFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberBirthDate, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_withObjectContainingDifferentProperty_returnsFalse() {
        assertNotEquals(testMemberBirthDate, MemberBirthDate.create(LocalDate.MIN));
    }
}