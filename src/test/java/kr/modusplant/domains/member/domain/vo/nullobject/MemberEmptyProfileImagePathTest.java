package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.common.util.domain.vo.nullobject.MemberEmptyProfileImagePathTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileImagePathTestUtils.testMemberProfileImagePath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MemberEmptyProfileImagePathTest implements MemberEmptyProfileImagePathTestUtils {
    @Test
    @DisplayName("create로 비어 있는 회원 프로필 이미지 경로 반환")
    void testCreate_givenNothing_willReturnMemberEmptyProfileImagePath() {
        assertThat(MemberEmptyProfileImagePath.create()).isEqualTo(MemberEmptyProfileImagePath.create());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberEmptyProfileImagePath, testMemberEmptyProfileImagePath);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberEmptyProfileImagePath, testMemberId);
    }

    @Test
    @DisplayName("MemberProfileImagePath 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testMemberEmptyProfileImagePath, testMemberProfileImagePath);
    }
}