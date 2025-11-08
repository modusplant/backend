package kr.modusplant.domains.member.domain.entity;

import kr.modusplant.domains.member.common.util.domain.entity.MemberProfileImageTestUtils;
import kr.modusplant.domains.member.domain.exception.EmptyMemberProfileImageBytesException;
import kr.modusplant.domains.member.domain.exception.EmptyMemberProfileImagePathException;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.domain.vo.MemberProfileImagePath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.member.adapter.util.MemberProfileImageUtils.generateMemberProfileImagePath;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberProfileImageTest implements MemberProfileImageTestUtils {
    @DisplayName("null 값으로 create 호출")
    @Test
    void testCreate_givenNullToOneOfTwoParameters_willThrowException() {
        // MemberProfileImagePath가 null일 때
        // given
        EmptyMemberProfileImagePathException emptyMemberProfileImagePathException = assertThrows(EmptyMemberProfileImagePathException.class, () -> MemberProfileImage.create(null, testMemberProfileImageBytes));

        // when & then
        assertThat(emptyMemberProfileImagePathException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_PROFILE_IMAGE_PATH);

        // MemberProfileImageBytes가 null일 때
        // given
        EmptyMemberProfileImageBytesException emptyMemberProfileImageBytesException = assertThrows(EmptyMemberProfileImageBytesException.class, () -> MemberProfileImage.create(testMemberProfileImagePath, null));

        // when & then
        assertThat(emptyMemberProfileImageBytesException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_PROFILE_IMAGE_BYTES);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberProfileImage, testMemberProfileImage);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberProfileImage, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testMemberProfileImage, MemberProfileImage.create(MemberProfileImagePath.create(generateMemberProfileImagePath(UUID.randomUUID(), "image.png")), testMemberProfileImageBytes));
    }
}