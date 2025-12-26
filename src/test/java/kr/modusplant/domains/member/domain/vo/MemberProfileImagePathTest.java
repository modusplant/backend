package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.EmptyMemberProfileImagePathException;
import kr.modusplant.domains.member.domain.exception.InvalidMemberProfileImagePathException;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.member.adapter.util.MemberProfileImageUtils.generateMemberProfileImagePath;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileImagePathTestUtils.testMemberProfileImagePath;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberProfileImagePathTest {
    @Test
    @DisplayName("create으로 회원 프로필 이미지 경로 반환")
    void testCreate_givenValidValue_willReturnMemberProfileImagePath() {
        assertThat(MemberProfileImagePath.create(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH)).isEqualTo(MemberProfileImagePath.create(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyMemberProfileImagePathException exception = assertThrows(EmptyMemberProfileImagePathException.class, () -> MemberProfileImagePath.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_PROFILE_IMAGE_PATH);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenEmptyString_willThrowException() {
        EmptyMemberProfileImagePathException exception = assertThrows(EmptyMemberProfileImagePathException.class, () -> MemberProfileImagePath.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_PROFILE_IMAGE_PATH);
    }

    @Test
    @DisplayName("정규 표현식에 매칭되지 않는 값으로 create을 호출하여 오류 발생")
    void testCreate_givenInvalidValue_willThrowException() {
        InvalidMemberProfileImagePathException exception = assertThrows(InvalidMemberProfileImagePathException.class, () -> MemberProfileImagePath.create("invalid-data"));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.INVALID_MEMBER_PROFILE_IMAGE_PATH);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberProfileImagePath, testMemberProfileImagePath);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberProfileImagePath, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testMemberProfileImagePath, MemberProfileImagePath.create(generateMemberProfileImagePath(UUID.randomUUID(), "image.png")));
    }
}