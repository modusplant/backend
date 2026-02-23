package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.common.util.domain.vo.nullobject.EmptyMemberProfileIntroductionTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileIntroductionTestUtils.testMemberProfileIntroduction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EmptyMemberProfileIntroductionTest implements EmptyMemberProfileIntroductionTestUtils {
    @Test
    @DisplayName("create로 비어 있는 회원 프로필 소개 반환")
    void testCreate_givenNothing_willReturnEmptyMemberProfileIntroduction() {
        assertThat(EmptyMemberProfileIntroduction.create()).isEqualTo(EmptyMemberProfileIntroduction.create());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(TEST_EMPTY_MEMBER_PROFILE_INTRODUCTION, TEST_EMPTY_MEMBER_PROFILE_INTRODUCTION);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(TEST_EMPTY_MEMBER_PROFILE_INTRODUCTION, testMemberId);
    }

    @Test
    @DisplayName("MemberProfileIntroduction 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(TEST_EMPTY_MEMBER_PROFILE_INTRODUCTION, testMemberProfileIntroduction);
    }
}