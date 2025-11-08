package kr.modusplant.domains.member.domain.entity.nullobject;

import kr.modusplant.domains.member.common.util.domain.entity.MemberProfileImageTestUtils;
import kr.modusplant.domains.member.common.util.domain.entity.nullobject.MemberEmptyProfileImageTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MemberEmptyProfileImageTest implements MemberEmptyProfileImageTestUtils, MemberProfileImageTestUtils {
    @Test
    @DisplayName("create로 비어 있는 회원 생일 반환")
    void testCreate_givenNothing_willReturnMemberEmptyProfileImage() {
        assertThat(MemberEmptyProfileImage.create()).isEqualTo(MemberEmptyProfileImage.create());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberEmptyProfileImage, testMemberEmptyProfileImage);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberEmptyProfileImage, testMemberProfileImagePath);
    }

    @Test
    @DisplayName("MemberProfileImage 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testMemberEmptyProfileImage, testMemberProfileImage);
    }
}