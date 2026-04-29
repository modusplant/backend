package kr.modusplant.domains.notification.domain.vo;

import kr.modusplant.domains.notification.domain.exception.EmptyValueException;
import kr.modusplant.shared.enums.NotificationActionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationActionTest {

    @Nested
    @DisplayName("액션 타입 판별 테스트")
    class ActionTypeCheckTests {

        @Test
        @DisplayName("POST_LIKED 액션은 게시글 관련 액션으로 판별된다")
        void testIsPostRelatedAction_givenPostLiked_willReturnTrue() {
            NotificationAction action = NotificationAction.create(NotificationActionType.POST_LIKED);
            assertTrue(action.isPostRelatedAction());
            assertFalse(action.isCommentRelatedAction());
        }

        @Test
        @DisplayName("댓글/답글 관련 액션들은 댓글 관련 액션으로 판별된다")
        void testIsCommentRelatedAction_givenCommentActions_willReturnTrue() {
            assertTrue(NotificationAction.create(NotificationActionType.COMMENT_ADDED).isCommentRelatedAction());
            assertTrue(NotificationAction.create(NotificationActionType.COMMENT_LIKED).isCommentRelatedAction());
            assertTrue(NotificationAction.create(NotificationActionType.COMMENT_REPLY_ADDED).isCommentRelatedAction());
        }
    }

    @Nested
    @DisplayName("액션 타입 생성 테스트")
    class createTest {

        @Test
        @DisplayName("NotificationAction 생성")
        void testCreate_givenValidValue_willReturnNotificationAction() {
            // when
            NotificationAction postLikeNotificationAction = NotificationAction.create(NotificationActionType.POST_LIKED);
            NotificationAction commentLikeNotificationAction = NotificationAction.create(NotificationActionType.COMMENT_LIKED);
            NotificationAction commentAddedNotificationAction = NotificationAction.create(NotificationActionType.COMMENT_ADDED);
            NotificationAction commentReplyAddedNotificationAction = NotificationAction.create(NotificationActionType.COMMENT_REPLY_ADDED);

            // then
            assertEquals(NotificationActionType.POST_LIKED, postLikeNotificationAction.getAction());
            assertEquals(NotificationActionType.COMMENT_LIKED, commentLikeNotificationAction.getAction());
            assertEquals(NotificationActionType.COMMENT_ADDED, commentAddedNotificationAction.getAction());
            assertEquals(NotificationActionType.COMMENT_REPLY_ADDED, commentReplyAddedNotificationAction.getAction());
        }

        @Test
        @DisplayName("null 액션 생성 시 EmptyValueException 발생")
        void testCreate_givenNull_willThrowException() {
            assertThrows(EmptyValueException.class, () -> NotificationAction.create(null));
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("액션 타입이 같으면 equals는 true를 반환한다")
        void useEqual_givenSameActionType_willReturnTrue() {
            NotificationAction action1 = NotificationAction.create(NotificationActionType.POST_LIKED);
            NotificationAction action2 = NotificationAction.create(NotificationActionType.POST_LIKED);

            assertEquals(action1, action2);
            assertEquals(action1.hashCode(), action2.hashCode());
        }

        @Test
        @DisplayName("액션 타입이 다르면 equals는 false를 반환한다")
        void useEqual_givenDifferentActionType_willReturnFalse() {
            NotificationAction action1 = NotificationAction.create(NotificationActionType.POST_LIKED);
            NotificationAction action2 = NotificationAction.create(NotificationActionType.COMMENT_ADDED);

            assertNotEquals(action1, action2);
        }
    }

}