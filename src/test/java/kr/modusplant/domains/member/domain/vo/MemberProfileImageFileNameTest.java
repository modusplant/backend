package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.UnsupportedFileException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_FILE_NAME;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileImageFileNameTestUtils.testMemberProfileImageFileName;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileImageFileNameTestUtils.testMemberProfileImageFileName2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberProfileImageFileNameTest {
    @Test
    @DisplayName("create으로 회원 프로필 이미지 파일명 반환")
    void testCreate_givenValidValue_willReturnMemberProfileImageFileName() {
        assertThat(MemberProfileImageFileName.create(MEMBER_PROFILE_BASIC_USER_IMAGE_FILE_NAME)).isEqualTo(MemberProfileImageFileName.create(MEMBER_PROFILE_BASIC_USER_IMAGE_FILE_NAME));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> MemberProfileImageFileName.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_PROFILE_IMAGE_FILE_NAME);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenEmptyString_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> MemberProfileImageFileName.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_MEMBER_PROFILE_IMAGE_FILE_NAME);
    }

    @Test
    @DisplayName("파일 확장자를 포함하지 않는 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenStringNotContainingExtension_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> MemberProfileImageFileName.create("InvalidData"));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.INVALID_MEMBER_PROFILE_IMAGE_FILE_NAME);
    }

    @Test
    @DisplayName("지원하지 않는 확장자를 포함하는 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenUnsupportedExtension_willThrowException() {
        assertThrows(UnsupportedFileException.class, () -> MemberProfileImageFileName.create("image.pdf"));
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberProfileImageFileName, testMemberProfileImageFileName);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberProfileImageFileName, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testMemberProfileImageFileName, testMemberProfileImageFileName2);
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testMemberProfileImageFileName.hashCode(), testMemberProfileImageFileName.hashCode());
    }
}
