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
    class createTests {
        @Test
        @DisplayName("유효한 NotificationStatusType으로 객체를 생성한다")
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
        @DisplayName("statusType이 null인 경우 EmptyValueException을 던진다")
        void testCreate_givenNull_willThrowException() {
            // when & then
            EmptyValueException exception = assertThrows(EmptyValueException.class,
                    () -> NotificationStatus.create(null));

            assertEquals(NotificationErrorCode.EMPTY_NOTIFICATION_STATUS, exception.getErrorCode());
        }
    }


    @Nested
    @DisplayName("NotificationStatus read/unread 테스트")
    class readUnreadTests {

        @Test
        @DisplayName("read() 메서드로 읽음 상태를 생성한다")
        void testRead_givenNoParameter_willReturnNotificationStatus() {
            // when
            NotificationStatus status = NotificationStatus.read();

            // then
            assertNotNull(status);
            assertTrue(status.isRead());
            assertFalse(status.isUnread());
        }

        @Test
        @DisplayName("unread() 메서드로 안읽음 상태를 생성한다")
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
        @DisplayName("같은 객체에 대한 equals 호출")
        void useEqual_givenSameObject_willReturnTrue() {
            // when & then
            assertEquals(testNotificationStatusUnread, testNotificationStatusUnread);
            assertEquals(testNotificationStatusUnread.hashCode(), testNotificationStatusUnread.hashCode());
        }

        @Test
        @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
            // when & then
            assertNotEquals(testNotificationStatusUnread, testNotificationId);
        }

        @Test
        @DisplayName("다른 상태를 갖는 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
            // when & then
            assertNotEquals(testNotificationStatusUnread, NotificationStatus.read());
            assertNotEquals(testNotificationStatusRead, NotificationStatus.unread());
        }
    }
}