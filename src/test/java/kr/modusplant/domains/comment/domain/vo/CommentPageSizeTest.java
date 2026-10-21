package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.common.util.domain.vo.CommentPageSizeTestUtils;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PAGE_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommentPageSizeTest implements CommentPageSizeTestUtils {

    @Test
    @DisplayName("null인 페이지 크기로 생성 시 예외 반환")
    void testCreate_givenNullValue_willThrowException() {
        // given & when
        EmptyValueException result = assertThrows(EmptyValueException.class, () -> CommentPageSize.create(null));

        // then
        assertThat(result.getErrorCode()).isEqualTo(CommentErrorCode.EMPTY_COMMENT_PAGE_SIZE);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 51})
    @DisplayName("범위를 벗어난 페이지 크기로 생성 시 예외 반환")
    void testCreate_givenOutOfRangeValue_willThrowException(int value) {
        // given & when
        InvalidValueException result = assertThrows(InvalidValueException.class, () -> CommentPageSize.create(value));

        // then
        assertThat(result.getErrorCode()).isEqualTo(CommentErrorCode.COMMENT_PAGE_SIZE_OUT_OF_RANGE);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 50})
    @DisplayName("범위 경계의 페이지 크기로 생성 시 CommentPageSize 반환")
    void testCreate_givenBoundaryValue_willReturnCommentPageSize(int value) {
        // given & when
        CommentPageSize result = CommentPageSize.create(value);

        // then
        assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("같은 값의 페이지 크기와 비교 시 True 반환")
    void testEquals_givenSameValue_willReturnTrue() {
        // given
        CommentPageSize other = CommentPageSize.create(TEST_COMMENT_PAGE_SIZE);

        // when
        boolean result = testCommentPageSize.equals(other);

        // then
        assertThat(result).isTrue();
        assertThat(testCommentPageSize.hashCode()).isEqualTo(other.hashCode());
    }

    @Test
    @DisplayName("다른 값의 페이지 크기와 비교 시 False 반환")
    void testEquals_givenDifferentValue_willReturnFalse() {
        // given
        CommentPageSize other = CommentPageSize.create(TEST_COMMENT_PAGE_SIZE + 1);

        // when
        boolean result = testCommentPageSize.equals(other);

        // then
        assertThat(result).isFalse();
    }
}
