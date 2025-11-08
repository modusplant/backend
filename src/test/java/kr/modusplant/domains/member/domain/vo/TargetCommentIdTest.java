package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.EmptyTargetCommentPathException;
import kr.modusplant.domains.member.domain.exception.EmptyTargetPostIdException;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.TargetCommentIdTestUtils.testTargetCommentId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetCommentPathTestUtils.testTargetCommentPath;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetPostIdTestUtils.testTargetPostId;
import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TargetCommentIdTest {
    @DisplayName("null 값으로 create 호출")
    @Test
    void testCreate_givenNullToOneOfTwoParameters_willThrowException() {
        // TargetPostId가 null일 때
        // given
        EmptyTargetPostIdException emptyTargetPostIdException = assertThrows(EmptyTargetPostIdException.class, () -> TargetCommentId.create(null, testTargetCommentPath));

        // when & then
        assertThat(emptyTargetPostIdException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_TARGET_POST_ID);

        // TargetCommentPath가 null일 때
        // given
        EmptyTargetCommentPathException emptyTargetCommentPathException = assertThrows(EmptyTargetCommentPathException.class, () -> TargetCommentId.create(testTargetPostId, null));

        // when & then
        assertThat(emptyTargetCommentPathException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_TARGET_COMMENT_PATH);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testTargetCommentId, testTargetCommentId);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testTargetCommentId, testTargetPostId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testTargetCommentId, TargetCommentId.create(testTargetPostId, TargetCommentPath.create(TEST_COMM_COMMENT_PATH + "1")));
    }
}