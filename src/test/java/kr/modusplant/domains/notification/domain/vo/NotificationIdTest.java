package kr.modusplant.domains.notification.domain.vo;

import kr.modusplant.domains.notification.common.util.domain.aggregate.NotificationTestUtils;
import kr.modusplant.domains.notification.domain.exception.EmptyValueException;
import kr.modusplant.domains.notification.domain.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.generator.UlidIdGenerator;
import kr.modusplant.shared.generator.UlidGeneratorHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import static kr.modusplant.shared.persistence.common.util.constant.NotificationConstant.TEST_NOTIFICATION_ULID;
import static org.junit.jupiter.api.Assertions.*;

class NotificationIdTest implements NotificationTestUtils {
    @SuppressWarnings("unused")
    private final UlidGeneratorHolder ulidGeneratorHolder = new UlidGeneratorHolder(new UlidIdGenerator());

    @Nested
    @DisplayName("NotificationId generate 테스트")
    class GenerateTests {

        @Test
        @DisplayName("generate() 메서드로 유효한 NotificationId를 생성한다")
        void testGenerate_givenNoParameter_willReturnNotificationId() {
            // when
            NotificationId notificationId = NotificationId.generate();

            // then
            assertNotNull(notificationId);
            assertNotNull(notificationId.getValue());
            assertEquals(26, notificationId.getValue().length());
            assertTrue(notificationId.getValue().matches("^[0-9A-HJKMNP-TV-Z]{26}$"));
        }
    }

    @Nested
    @DisplayName("NotificationId create 테스트")
    class CreateTests {

        @Test
        @DisplayName("유효한 ULID 문자열로 NotificationId를 생성한다")
        void testCreate_givenUlid_willReturnNotificationId() {
            // then
            assertFalse(StringUtils.isBlank(testNotificationId.getValue()));
            assertFalse(testNotificationId.getValue().length() != 26);
            assertTrue(testNotificationId.getValue().matches("^[0-9A-HJKMNP-TV-Z]{26}$"));
            assertNotNull(testNotificationId);
            assertEquals(TEST_NOTIFICATION_ULID, testNotificationId.getValue());
        }

        @Test
        @DisplayName("null이나 빈 문자열 ULID로 NotificationId 생성 시 EmptyValueException을 발생시킨다")
        void testCreate_givenNullOrEmptyNotificationId_willThrowException() {
            // when & then
            assertThrows(EmptyValueException.class, () -> NotificationId.create(null));
            assertThrows(EmptyValueException.class, () -> NotificationId.create(""));
            assertThrows(EmptyValueException.class, () -> NotificationId.create("   "));
        }

        @Test
        @DisplayName("유효하지 않은 ULID로 NotificationId 생성 시 InvalidValueException을 발생시킨다")
        void shouldThrowInvalidNotificationIdExceptionWhenUlidIsInvalid() {
            // when & then
            assertThrows(InvalidValueException.class, () -> NotificationId.create("01K59D7R5ZT51X9HVZXGK4A6W")); // 25자
            assertThrows(InvalidValueException.class, () -> NotificationId.create("01K59D7R5ZT51X9HVZXGK4A6W@")); // 유효하지 않은 문자 @포함
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("같은 객체에 대한 equals 호출")
        void useEqual_givenSameObject_willReturnTrue() {
            // when & then
            assertEquals(testNotificationId, testNotificationId);
            assertEquals(testNotificationId.hashCode(), testNotificationId.hashCode());
        }

        @Test
        @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
            // when & then
            assertNotEquals(testNotificationId, testNotificationActionCommentAdded);
        }

        @Test
        @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
            // when & then
            assertNotEquals(testNotificationId, NotificationId.generate());
        }
    }
}