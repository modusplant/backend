package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectCommentPathTestUtils.testActivitySubjectCommentPath;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberStatusTestUtils.testMemberActiveStatus;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.INVALID_ACTIVITY_SUBJECT_COMMENT_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ActivitySubjectCommentPathTest {
    @Test
    @DisplayName("create으로 대상 댓글 아이디 반환")
    void testCreate_givenValidValue_willReturnActivitySubjectCommentPath() {
        assertNotNull(ActivitySubjectCommentPath.create(TEST_COMM_COMMENT_PATH).getValue());
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> ActivitySubjectCommentPath.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_ACTIVITY_SUBJECT_COMMENT_PATH);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> ActivitySubjectCommentPath.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_ACTIVITY_SUBJECT_COMMENT_PATH);
    }

    @Test
    @DisplayName("정규 표현식에 매칭되지 않는 값으로 create을 호출하여 오류 발생")
    void testCreate_givenInvalidId_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> ActivitySubjectCommentPath.create("!유효하지않음!"));
        assertThat(exception.getErrorCode()).isEqualTo(INVALID_ACTIVITY_SUBJECT_COMMENT_PATH);
        assertThat(exception.getValueNames()).contains("activitySubjectCommentPath");
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testActivitySubjectCommentPath, testActivitySubjectCommentPath);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testActivitySubjectCommentPath, testMemberActiveStatus);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testActivitySubjectCommentPath, "1".repeat(16));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testActivitySubjectCommentPath.hashCode(), testActivitySubjectCommentPath.hashCode());
    }
}