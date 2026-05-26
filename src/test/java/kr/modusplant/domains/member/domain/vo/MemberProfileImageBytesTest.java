package kr.modusplant.domains.member.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileImageBytesTestUtils.testMemberProfileImageBytes;
import static kr.modusplant.domains.member.common.util.domain.vo.nullobject.EmptyMemberProfileImageBytesTestUtils.testEmptyMemberProfileImageBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MemberProfileImageBytesTest {
    @Test
    @DisplayName("create으로 회원 프로필 이미지 바이트 반환")
    void testCreate_givenValidValue_willReturnMemberProfileImageBytes() {
        assertThat(MemberProfileImageBytes.create(MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES)).isEqualTo(MemberProfileImageBytes.create(MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        assertThat(MemberProfileImageBytes.create(null)).isEqualTo(testEmptyMemberProfileImageBytes);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberProfileImageBytes, testMemberProfileImageBytes);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberProfileImageBytes, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testMemberProfileImageBytes, MemberProfileImageBytes.create((Arrays.toString(MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES) + "Test").getBytes()));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testMemberProfileImageBytes.hashCode(), testMemberProfileImageBytes.hashCode());
    }
}