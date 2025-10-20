package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.common.util.domain.vo.TargetCommentPathTestUtils;
import kr.modusplant.domains.member.domain.exception.EmptyTargetCommentPathException;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.InvalidDataException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_TARGET_COMMENT_PATH_STRING;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberBirthDateTestUtils.testMemberBirthDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TargetCommentPathTest implements TargetCommentPathTestUtils {
    @Test
    @DisplayName("create으로 대상 게시글 아이디 반환")
    void testCreate_givenValidValue_willReturnTargetPath() {
        assertNotNull(TargetCommentPath.create(TEST_TARGET_COMMENT_PATH_STRING).getValue());
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyTargetCommentPathException exception = assertThrows(EmptyTargetCommentPathException.class, () -> TargetCommentPath.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_TARGET_COMMENT_PATH);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_willThrowException() {
        EmptyTargetCommentPathException exception = assertThrows(EmptyTargetCommentPathException.class, () -> TargetCommentPath.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_TARGET_COMMENT_PATH);
    }

    @Test
    @DisplayName("정규 표현식에 매칭되지 않는 값으로 create을 호출하여 오류 발생")
    void testCreate_givenInvalidId_willThrowException() {
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> TargetCommentPath.create("!유효하지않음!"));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT);
        assertThat(exception.getDataName()).isEqualTo("targetCommentPath");
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testTargetCommentPath, testTargetCommentPath);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testTargetCommentPath, testMemberBirthDate);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testTargetCommentPath, "1".repeat(16));
    }
}