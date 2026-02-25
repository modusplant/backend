package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.common.util.domain.vo.nullobject.EmptyMemberProfileImagePathTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileImagePathTestUtils.testMemberProfileImagePath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EmptyMemberProfileImagePathTest implements EmptyMemberProfileImagePathTestUtils {
    @Test
    @DisplayName("create로 비어 있는 회원 프로필 이미지 경로 반환")
    void testCreate_givenNothing_willReturnEmptyMemberProfileImagePath() {
        assertThat(EmptyMemberProfileImagePath.create()).isEqualTo(EmptyMemberProfileImagePath.create());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(TEST_EMPTY_MEMBER_PROFILE_IMAGE_PATH, TEST_EMPTY_MEMBER_PROFILE_IMAGE_PATH);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(TEST_EMPTY_MEMBER_PROFILE_IMAGE_PATH, testMemberId);
    }

    @Test
    @DisplayName("MemberProfileImagePath 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(TEST_EMPTY_MEMBER_PROFILE_IMAGE_PATH, testMemberProfileImagePath);
    }
}