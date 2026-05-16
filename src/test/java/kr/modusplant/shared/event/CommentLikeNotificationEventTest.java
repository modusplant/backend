package kr.modusplant.shared.event;

import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.NotificationConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CommentLikeNotificationEventTest {

    @Nested
    @DisplayName("create 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 파라미터로 객체 생성 성공")
        void testCreate_givenValidParameters_willReturnEvent() {
            CommentLikeNotificationEvent event = CommentLikeNotificationEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID, TEST_NOTIFICATION_COMMENT_PATH_DEPTH3);

            assertNotNull(event);
            assertEquals(TEST_NOTIFICATION_ACTOR_ID, event.getActorId());
            assertEquals(TEST_NOTIFICATION_POST_ULID, event.getPostUlid());
            assertEquals(TEST_NOTIFICATION_COMMENT_PATH_DEPTH3, event.getCommentPath());
        }

        @Test
        @DisplayName("actorId가 null일 때 오류 발생")
        void testCreate_givenNullActorId_willThrowException() {
            InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                    CommentLikeNotificationEvent.create(null, TEST_NOTIFICATION_POST_ULID, TEST_NOTIFICATION_COMMENT_PATH_DEPTH3));

            assertThat(exception.getMessage()).contains("NOT_FOUND_ACTOR");
        }

        @Test
        @DisplayName("postUlid가 null일 때 오류 발생")
        void testCreate_givenNullPostUlid_willThrowException() {
            InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                    CommentLikeNotificationEvent.create(TEST_NOTIFICATION_ACTOR_ID, null, TEST_NOTIFICATION_COMMENT_PATH_DEPTH3));

            assertThat(exception.getMessage()).contains("NOT_FOUND_POST");
        }

        @Test
        @DisplayName("commentPath가 비어 있을 때 오류 발생")
        void testCreate_givenEmptyCommentPath_willThrowException() {
            InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                    CommentLikeNotificationEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID, " "));

            assertThat(exception.getMessage()).contains("NOT_FOUND_COMMENT");
        }

    }
}