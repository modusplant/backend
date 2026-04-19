package kr.modusplant.domains.notification.domain.vo;

import kr.modusplant.domains.notification.common.util.domain.aggregate.NotificationTestUtils;
import kr.modusplant.domains.notification.domain.exception.EmptyValueException;
import kr.modusplant.domains.notification.domain.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_RECIPIENT_ID;
import static org.junit.jupiter.api.Assertions.*;

class RecipientIdTest implements NotificationTestUtils {

    @Nested
    @DisplayName("RecipientId fromUuid 테스트")
    class FromUuidTests {

        @Test
        @DisplayName("유효한 UUID로 RecipientId를 생성한다")
        void testFromUuid_givenValidUuid_willReturnRecipientId() {
            // when
            RecipientId recipientId = RecipientId.fromUuid(TEST_NOTIFICATION_RECIPIENT_ID);

            // then
            assertNotNull(recipientId);
            assertNotNull(recipientId.getValue());
            assertEquals(TEST_NOTIFICATION_RECIPIENT_ID, recipientId.getValue());
        }

        @Test
        @DisplayName("null UUID로 RecipientId 생성 시 EmptyValueException을 발생시킨다")
        void testFromUuid_givenNull_willThrowException() {
            // when & then
            EmptyValueException exception = assertThrows(EmptyValueException.class, () -> RecipientId.fromUuid(null));
            assertNotNull(exception);
        }
    }

    @Nested
    @DisplayName("RecipientId fromString 테스트")
    class FromStringTests {

        @Test
        @DisplayName("유효한 UUID 문자열로 RecipientId를 생성한다")
        void testFromString_givenValidUuidString_willReturnRecipientId() {
            // when
            RecipientId recipientId = RecipientId.fromString(TEST_NOTIFICATION_RECIPIENT_ID.toString());

            // then
            assertNotNull(recipientId);
            assertEquals(TEST_NOTIFICATION_RECIPIENT_ID, recipientId.getValue());
        }

        @Test
        @DisplayName("null이나 빈 문자열로 RecipientId 생성 시 EmptyValueException을 발생시킨다")
        void testFromString_givenNullOrEmpty_willThrowException() {
            // when & then
            assertThrows(EmptyValueException.class, () -> RecipientId.fromString(null));
            assertThrows(EmptyValueException.class, () -> RecipientId.fromString(""));
            assertThrows(EmptyValueException.class, () -> RecipientId.fromString("   "));
        }

        @Test
        @DisplayName("유효하지 않은 UUID 문자열로 RecipientId 생성 시 InvalidValueException을 발생시킨다")
        void testFromString_givenInvalidUuidString_willThrowException() {
            // when & then
            InvalidValueException exception = assertThrows(InvalidValueException.class, () -> RecipientId.fromString("faejlfjakwefjlwkajf"));
            assertNotNull(exception);
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("같은 객체에 대한 equals 호출")
        void useEqual_givenSameObject_willReturnTrue() {
            // when & then
            assertEquals(testRecipientId, testRecipientId);
            assertEquals(testRecipientId.hashCode(), testRecipientId.hashCode());
        }

        @Test
        @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
            // when & then
            assertNotEquals(testRecipientId, UUID.randomUUID());
        }

        @Test
        @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
            // when & then
            assertNotEquals(testRecipientId, RecipientId.fromUuid(UUID.randomUUID()));
        }
    }
}