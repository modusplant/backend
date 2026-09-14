package kr.modusplant.domains.notification.domain.aggregate;

import kr.modusplant.domains.notification.common.util.domain.aggregate.NotificationTestUtils;
import kr.modusplant.domains.notification.domain.exception.EmptyValueException;
import kr.modusplant.domains.notification.domain.vo.NotificationId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest implements NotificationTestUtils {

    @Nested
    @DisplayName("Notification 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("유효한 파라미터로 Notification 반환")
        void testCreate_givenValidParameters_willReturnNotification() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // when
            Notification notification = createPostLikedReadNotification(now);

            // then
            assertNotNull(notification);
            assertEquals(testNotificationId, notification.getNotificationId());
            assertEquals(testRecipientId, notification.getRecipientId());
            assertEquals(testActor, notification.getActor());
            assertEquals(testNotificationActionPostLiked, notification.getAction());
            assertEquals(testNotificationStatusRead, notification.getStatus());
            assertEquals(testPostId, notification.getPostId());
            assertEquals(now, notification.getCreatedAt());
        }

        @Test
        @DisplayName("필수 파라미터가 null일 때 예외 반환")
        void testCreate_givenNullParameters_willThrowException() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // when & then
            assertThrows(EmptyValueException.class, () ->
                    Notification.create(null, testRecipientId, testActor, testNotificationActionPostLiked, testNotificationStatusRead, testPostId, null, testPostContentPreview, now));
            assertThrows(EmptyValueException.class, () ->
                    Notification.create(testNotificationId, testRecipientId, null, testNotificationActionPostLiked, testNotificationStatusRead, testPostId, null, testPostContentPreview, now));
            assertThrows(EmptyValueException.class, () ->
                    Notification.create(testNotificationId, testRecipientId, testActor, testNotificationActionPostLiked, testNotificationStatusRead, testPostId, null, testPostContentPreview, null));
        }

        @Test
        @DisplayName("댓글 액션인데 commentPath가 null일 때 예외 반환")
        void testCreate_givenCommentActionWithNullPath_willThrowException() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // when & then
            assertThrows(EmptyValueException.class, () ->
                    Notification.create(testNotificationId, testRecipientId, testActor, testNotificationActionCommentLiked, testNotificationStatusRead, testPostId, null, testCommentContentPreview, now));
        }
    }

    @Nested
    @DisplayName("Notification 초기 생성(createInitial) 테스트")
    class CreateInitialTests {

        @Test
        @DisplayName("유효한 파라미터로 초기 Notification 반환")
        void testCreateInitial_givenValidParameters_willReturnNotification() {
            // when
            Notification notification = Notification.createInitial(testRecipientId, testActor, testNotificationActionPostLiked, testNotificationStatusUnread, testPostId, null, testPostContentPreview);

            // then
            assertNotNull(notification);
            assertNull(notification.getNotificationId());
            assertNotNull(notification.getCreatedAt());
            assertEquals(testNotificationStatusUnread, notification.getStatus());
        }
    }

    @Nested
    @DisplayName("Notification 기능 테스트")
    class FunctionTests {

        @Test
        @DisplayName("읽음 처리 활동 수행")
        void testRead_givenUnreadNotification_willProcessAction() {
            // given
            Notification notification = createPostLikedUnreadNotification(LocalDateTime.now());

            // when
            notification.read();

            // then
            assertTrue(notification.getStatus().isRead());
        }

        @Test
        @DisplayName("30일 이전 생성일일 때 참 반환")
        void testIsExpired_givenOldNotification_willReturnTrue() {
            // given
            Notification oldNotification = createPostLikedReadNotification(LocalDateTime.now().minusDays(31));

            // when & then
            assertTrue(oldNotification.isExpired());
        }

        @Test
        @DisplayName("30일 이내 생성일일 때 거짓 반환")
        void testIsExpired_givenRecentNotification_willReturnFalse() {
            // given
            Notification recentNotification = createPostLikedReadNotification(LocalDateTime.now().minusDays(10));

            // when & then
            assertFalse(recentNotification.isExpired());
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("동일 NotificationId일 때 참 반환")
        void testEquals_givenSameId_willReturnTrue() {
            LocalDateTime now = LocalDateTime.now();
            Notification n1 = createPostLikedReadNotification(now);
            Notification n2 = createPostLikedReadNotification(now);

            assertEquals(n1, n2);
            assertEquals(n1.hashCode(), n2.hashCode());
        }

        @Test
        @DisplayName("다른 NotificationId일 때 거짓 반환")
        void testEquals_givenDifferentId_willReturnFalse() {
            // given
            Notification n1 = createPostLikedReadNotification(LocalDateTime.now());
            NotificationId notificationId2 = NotificationId.create("71K59D7R5ZT51X9HVZXGK4A6WN");

            // when
            Notification n2 = Notification.create(notificationId2, testRecipientId, testActor, testNotificationActionPostLiked, testNotificationStatusRead, testPostId, null, testPostContentPreview, LocalDateTime.now());

            // then
            assertNotEquals(n1, n2);
        }
    }

}