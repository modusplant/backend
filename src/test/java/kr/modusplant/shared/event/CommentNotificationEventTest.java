package kr.modusplant.shared.event;

import kr.modusplant.domains.notification.domain.enums.NotificationActionType;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.notification.common.constant.NotificationConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentNotificationEventTest {

    @Nested
    @DisplayName("create 테스트")
    class CreateTest {

        @Test
        @DisplayName("commentPath에 '.'이 없으면 COMMENT_ADDED 액션으로 생성")
        void testCreate_givenRootPath_willHaveCommentAddedAction() {
            // when
            CommentNotificationEvent event = CommentNotificationEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID, TEST_NOTIFICATION_COMMENT_PATH_DEPTH1, TEST_NOTIFICATION_COMMENT_PREVIEW);

            // then
            assertEquals(NotificationActionType.COMMENT_ADDED.name(), event.getAction());
            assertEquals(TEST_NOTIFICATION_ACTOR_ID, event.getActorId());
            assertEquals(TEST_NOTIFICATION_POST_ULID, event.getPostUlid());
            assertEquals(TEST_NOTIFICATION_COMMENT_PATH_DEPTH1, event.getCommentPath());
            assertEquals(TEST_NOTIFICATION_COMMENT_PREVIEW, event.getContentPreview());
        }

        @Test
        @DisplayName("commentPath에 '.'이 포함되면 COMMENT_REPLY_ADDED 액션으로 생성")
        void testCreate_givenChildPath_willHaveCommentReplyAddedAction() {
            // when
            CommentNotificationEvent event = CommentNotificationEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID, TEST_NOTIFICATION_COMMENT_PATH_DEPTH3, TEST_NOTIFICATION_COMMENT_PREVIEW);

            // then
            assertEquals(NotificationActionType.COMMENT_REPLY_ADDED.name(), event.getAction());
            assertEquals(TEST_NOTIFICATION_ACTOR_ID, event.getActorId());
            assertEquals(TEST_NOTIFICATION_POST_ULID, event.getPostUlid());
            assertEquals(TEST_NOTIFICATION_COMMENT_PATH_DEPTH3, event.getCommentPath());
            assertEquals(TEST_NOTIFICATION_COMMENT_PREVIEW, event.getContentPreview());
        }


        @Test
        @DisplayName("actorId가 null일 때 오류 발생")
        void testCreate_givenNullActorId_willThrowException() {
            assertThrows(InvalidValueException.class, () ->
                    CommentNotificationEvent.create(null,  TEST_NOTIFICATION_POST_ULID, TEST_NOTIFICATION_COMMENT_PATH_DEPTH3, TEST_NOTIFICATION_COMMENT_PREVIEW));
        }

        @Test
        @DisplayName("commentPath가 null이거나 비어 있을 때 오류 발생")
        void testCreate_givenEmptyCommentPath_willThrowException() {
            InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                    CommentNotificationEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID,"", TEST_NOTIFICATION_COMMENT_PREVIEW));

            assertThat(exception.getMessage()).contains("NOT_FOUND_COMMENT");
        }

    }

}