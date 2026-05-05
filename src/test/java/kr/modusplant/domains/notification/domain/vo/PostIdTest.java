package kr.modusplant.domains.notification.domain.vo;

import kr.modusplant.domains.notification.domain.exception.EmptyValueException;
import kr.modusplant.domains.notification.domain.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_POST_ULID;
import static org.junit.jupiter.api.Assertions.*;

class PostIdTest {

    @Nested
    @DisplayName("generate 테스트")
    class GenerateTests {
        @Test
        @DisplayName("generate 메서드는 유효한 ULID를 생성한다")
        void testGenerate_willCreateValidPostId() {
            PostId postId = PostId.generate();
            assertNotNull(postId.getValue());
            assertEquals(26, postId.getValue().length());
        }
    }

    @Nested
    @DisplayName("create 테스트")
    class CreateTests {

        @Test
        @DisplayName("유효한 26자 ULID로 PostId를 생성한다")
        void testCreate_givenValidUlid_willReturnPostId() {
            PostId postId = PostId.create(TEST_NOTIFICATION_POST_ULID);
            assertEquals(TEST_NOTIFICATION_POST_ULID, postId.getValue());
        }

        @Test
        @DisplayName("빈 값이나 null인 경우 EmptyValueException 발생")
        void testCreate_givenBlank_willThrowException() {
            assertThrows(EmptyValueException.class, () -> PostId.create(null));
            assertThrows(EmptyValueException.class, () -> PostId.create(""));
        }

        @Test
        @DisplayName("26자가 아니거나 ULID 패턴이 아니면 InvalidValueException 발생")
        void testCreate_givenInvalidPattern_willThrowException() {
            assertThrows(InvalidValueException.class, () -> PostId.create("short-id"));
            assertThrows(InvalidValueException.class, () -> PostId.create("invalid-pattern-1234567890123"));
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("ULID 값이 같으면 equals는 true를 반환한다")
        void useEqual_givenSameUlid_willReturnTrue() {
            PostId id1 = PostId.create(TEST_NOTIFICATION_POST_ULID);
            PostId id2 = PostId.create(TEST_NOTIFICATION_POST_ULID);

            assertEquals(id1, id2);
            assertEquals(id1.hashCode(), id2.hashCode());
        }

        @Test
        @DisplayName("다른 클래스나 null과 비교 시 false를 반환한다")
        void useEqual_givenNullOrDifferentClass_willReturnFalse() {
            PostId id = PostId.generate();

            assertNotEquals(id, null);
            assertNotEquals(id, "string-id");
        }
    }
}