package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.common.util.domain.vo.nullobject.EmptyMemberProfileImageBytesTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileImageBytesTestUtils.testMemberProfileImageBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EmptyMemberProfileImageBytesTest implements EmptyMemberProfileImageBytesTestUtils {
    @Test
    @DisplayName("create로 비어 있는 회원 프로필 이미지 바이트 반환")
    void testCreate_givenNothing_willReturnEmptyMemberProfileImageBytes() {
        assertThat(EmptyMemberProfileImageBytes.create()).isEqualTo(EmptyMemberProfileImageBytes.create());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(TEST_EMPTY_MEMBER_PROFILE_IMAGE_BYTES, TEST_EMPTY_MEMBER_PROFILE_IMAGE_BYTES);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(TEST_EMPTY_MEMBER_PROFILE_IMAGE_BYTES, testMemberId);
    }

    @Test
    @DisplayName("MemberProfileImageBytes 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(TEST_EMPTY_MEMBER_PROFILE_IMAGE_BYTES, testMemberProfileImageBytes);
    }
}