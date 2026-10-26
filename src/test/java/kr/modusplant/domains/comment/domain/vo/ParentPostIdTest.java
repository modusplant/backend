package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.common.util.domain.vo.ParentPostIdTestUtils;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParentPostIdTest implements ParentPostIdTestUtils {

    @Test
    @DisplayName("유효한 ULID로 부모 게시글 id 생성 시 ParentPostId 반환")
    void testCreate_givenValidUlid_willReturnParentPostId() {
        // when
        ParentPostId result = ParentPostId.create(TEST_POST_ULID);

        // then
        assertThat(result.getValue()).isEqualTo(TEST_POST_ULID);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("비어 있는 값으로 부모 게시글 id 생성 시 예외 반환")
    void testCreate_givenBlankValue_willThrowException(String value) {
        // when
        EmptyValueException result = assertThrows(EmptyValueException.class, () -> ParentPostId.create(value));

        // then
        assertEquals(CommentErrorCode.EMPTY_POST_ID, result.getErrorCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"01JY3PPG5YJ41H7BPD0DSQW2R", "01JY3PPG5YJ41H7BPD0DSQW2RDA"})
    @DisplayName("길이가 26이 아닌 값으로 부모 게시글 id 생성 시 예외 반환")
    void testCreate_givenInvalidLength_willThrowException(String value) {
        // when
        InvalidValueException result = assertThrows(InvalidValueException.class, () -> ParentPostId.create(value));

        // then
        assertEquals(CommentErrorCode.INVALID_POST_ID, result.getErrorCode());
    }

    @Test
    @DisplayName("ULID 형식이 아닌 값으로 부모 게시글 id 생성 시 예외 반환")
    void testCreate_givenInvalidFormat_willThrowException() {
        // given
        String invalidUlid = "01JY3PPG5YJ41H7BPD0DSQW2R!";

        // when
        InvalidValueException result = assertThrows(InvalidValueException.class, () -> ParentPostId.create(invalidUlid));

        // then
        assertEquals(CommentErrorCode.INVALID_POST_ID, result.getErrorCode());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출 시 true 반환")
    void testEquals_givenSameObject_willReturnTrue() {
        // when & then
        //noinspection EqualsWithItself
        assertEquals(testParentPostId, testParentPostId);
    }

    @Test
    @DisplayName("같은 값을 가진 객체에 대한 equals 호출 시 true 반환")
    void testEquals_givenSameValue_willReturnTrue() {
        // given
        ParentPostId other = ParentPostId.create(TEST_POST_ULID);

        // when & then
        assertThat(testParentPostId).isEqualTo(other);
        assertThat(testParentPostId.hashCode()).isEqualTo(other.hashCode());
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출 시 false 반환")
    void testEquals_givenDifferentClassInstance_willReturnFalse() {
        // when & then
        assertThat(testParentPostId).isNotEqualTo(TEST_POST_ULID);
    }
}
