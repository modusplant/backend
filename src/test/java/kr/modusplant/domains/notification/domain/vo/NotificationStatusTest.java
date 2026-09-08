package kr.modusplant.domains.notification.domain.vo;

import kr.modusplant.domains.notification.common.util.domain.aggregate.NotificationTestUtils;
import kr.modusplant.domains.notification.domain.enums.NotificationStatusType;
import kr.modusplant.domains.notification.domain.exception.EmptyValueException;
import kr.modusplant.domains.notification.domain.exception.enums.NotificationErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationStatusTest implements NotificationTestUtils {

    @Nested
    @DisplayName("NotificationStatus create 테스트")
    class CreateTests {
        @Test
        @DisplayName("유효한 StatusType으로 NotificationStatus 반환")
        void testCreate_givenValidStatusType_willReturnNotificationStatus() {
            // given
            NotificationStatusType statusType = NotificationStatusType.READ;

            // when
            NotificationStatus status = NotificationStatus.create(statusType);

            // then
            assertNotNull(status);
            // 상태가 올바르게 반영되었는지 확인 (isRead 혹은 내부 필드 검증)
            assertTrue(status.isRead());
        }

        @Test
        @DisplayName("statusType이 null일 때 예외 반환")
        void testCreate_givenNull_willThrowException() {
            // when & then
            EmptyValueException exception = assertThrows(EmptyValueException.class,
                    () -> NotificationStatus.create(null));

            assertEquals(NotificationErrorCode.EMPTY_NOTIFICATION_STATUS, exception.getErrorCode());
        }
    }


    @Nested
    @DisplayName("NotificationStatus read/unread 테스트")
    class ReadUnreadTests {

        @Test
        @DisplayName("read()로 NotificationStatus 반환")
        void testRead_givenNoParameter_willReturnNotificationStatus() {
            // when
            NotificationStatus status = NotificationStatus.read();

            // then
            assertNotNull(status);
            assertTrue(status.isRead());
            assertFalse(status.isUnread());
        }

        @Test
        @DisplayName("unread()로 NotificationStatus 반환")
        void testUnread_givenNoParameter_willReturnNotificationStatus() {
            // when
            NotificationStatus status = NotificationStatus.unread();

            // then
            assertNotNull(status);
            assertTrue(status.isUnread());
            assertFalse(status.isRead());
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("같은 객체로 참 반환")
        void testEquals_givenSameObject_willReturnTrue() {
            // when & then
            assertEquals(testNotificationStatusUnread, testNotificationStatusUnread);
            assertEquals(testNotificationStatusUnread.hashCode(), testNotificationStatusUnread.hashCode());
        }

        @Test
        @DisplayName("다른 클래스 인스턴스로 거짓 반환")
        void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
            // when & then
            assertNotEquals(testNotificationStatusUnread, testNotificationId);
        }

        @Test
        @DisplayName("다른 상태일 때 거짓 반환")
        void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
            // when & then
            assertNotEquals(testNotificationStatusUnread, NotificationStatus.read());
            assertNotEquals(testNotificationStatusRead, NotificationStatus.unread());
        }
    }
}