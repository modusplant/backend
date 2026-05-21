package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectCommentIdTestUtils.testActivitySubjectCommentId;
import static kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectCommentPathTestUtils.testActivitySubjectCommentPath;
import static kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectPostIdTestUtils.testActivitySubjectPostId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ActivitySubjectCommentIdTest {
    @Test
    @DisplayName("create으로 타겟 댓글 ID 반환")
    void testCreate_givenValidValue_willReturnActivitySubjectCommentId() {
        assertThat(ActivitySubjectCommentId.create(testActivitySubjectPostId, testActivitySubjectCommentPath)).isEqualTo(ActivitySubjectCommentId.create(testActivitySubjectPostId, testActivitySubjectCommentPath));
    }

    @DisplayName("null 값으로 create 호출")
    @Test
    void testCreate_givenNullToOneOfTwoParameters_willThrowException() {
        // ActivitySubjectPostId가 null일 때
        // given
        EmptyValueException EmptyValueException = assertThrows(EmptyValueException.class, () -> ActivitySubjectCommentId.create(null, testActivitySubjectCommentPath));

        // when & then
        assertThat(EmptyValueException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_ACTIVITY_SUBJECT_POST_ID);

        // ActivitySubjectCommentPath가 null일 때
        // given
        EmptyValueException emptyActivitySubjectCommentPathException = assertThrows(EmptyValueException.class, () -> ActivitySubjectCommentId.create(testActivitySubjectPostId, null));

        // when & then
        assertThat(emptyActivitySubjectCommentPathException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_ACTIVITY_SUBJECT_COMMENT_PATH);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testActivitySubjectCommentId, testActivitySubjectCommentId);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testActivitySubjectCommentId, testActivitySubjectPostId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testActivitySubjectCommentId, ActivitySubjectCommentId.create(testActivitySubjectPostId, ActivitySubjectCommentPath.create(TEST_COMM_COMMENT_PATH + "1")));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testActivitySubjectCommentId.hashCode(), testActivitySubjectCommentId.hashCode());
    }
}