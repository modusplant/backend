package kr.modusplant.domains.notification.domain.vo;

import kr.modusplant.domains.notification.domain.exception.EmptyValueException;
import kr.modusplant.domains.notification.domain.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.notification.common.constant.NotificationConstant.TEST_NOTIFICATION_POST_ULID;
import static org.junit.jupiter.api.Assertions.*;

class PostIdTest {

    @Nested
    @DisplayName("generate 테스트")
    class GenerateTests {
        @Test
        @DisplayName("파라미터 없이 PostId 반환")
        void testGenerate_givenNoParameter_willReturnPostId() {
            PostId postId = PostId.generate();
            assertNotNull(postId.getValue());
            assertEquals(26, postId.getValue().length());
        }
    }

    @Nested
    @DisplayName("create 테스트")
    class CreateTests {

        @Test
        @DisplayName("유효한 ULID로 PostId 반환")
        void testCreate_givenValidUlid_willReturnPostId() {
            PostId postId = PostId.create(TEST_NOTIFICATION_POST_ULID);
            assertEquals(TEST_NOTIFICATION_POST_ULID, postId.getValue());
        }

        @Test
        @DisplayName("빈 값이나 null일 때 예외 반환")
        void testCreate_givenBlank_willThrowException() {
            assertThrows(EmptyValueException.class, () -> PostId.create(null));
            assertThrows(EmptyValueException.class, () -> PostId.create(""));
        }

        @Test
        @DisplayName("ULID 패턴이 아닐 때 예외 반환")
        void testCreate_givenInvalidPattern_willThrowException() {
            assertThrows(InvalidValueException.class, () -> PostId.create("short-id"));
            assertThrows(InvalidValueException.class, () -> PostId.create("invalid-pattern-1234567890123"));
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("ULID가 같을 때 참 반환")
        void testEquals_givenSameUlid_willReturnTrue() {
            PostId id1 = PostId.create(TEST_NOTIFICATION_POST_ULID);
            PostId id2 = PostId.create(TEST_NOTIFICATION_POST_ULID);

            assertEquals(id1, id2);
            assertEquals(id1.hashCode(), id2.hashCode());
        }

        @Test
        @DisplayName("null이나 다른 클래스일 때 거짓 반환")
        void testEquals_givenNullOrDifferentClass_willReturnFalse() {
            PostId id = PostId.generate();

            assertNotEquals(null, id);
            //noinspection AssertBetweenInconvertibleTypes
            assertNotEquals("string-id", id);
        }
    }
}