package kr.modusplant.domains.comment.domain.event;

import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.notification.domain.enums.NotificationActionType;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.notification.common.constant.NotificationConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentRegisterEventTest {

    @Nested
    @DisplayName("create 테스트")
    class CreateTest {

        @Test
        @DisplayName("루트 경로로 CommentRegisterEvent 반환")
        void testCreate_givenRootPath_willReturnCommentRegisterEvent() {
            // when
            CommentRegisterEvent event = CommentRegisterEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID, TEST_NOTIFICATION_COMMENT_PATH_DEPTH1, TEST_NOTIFICATION_COMMENT_PREVIEW);

            // then
            assertEquals(NotificationActionType.COMMENT_ADDED.name(), event.getAction());
            assertEquals(TEST_NOTIFICATION_ACTOR_ID, event.getAuthorId());
            assertEquals(TEST_NOTIFICATION_POST_ULID, event.getPostUlid());
            assertEquals(TEST_NOTIFICATION_COMMENT_PATH_DEPTH1, event.getCommentPath());
            assertEquals(TEST_NOTIFICATION_COMMENT_PREVIEW, event.getContentPreview());
        }

        @Test
        @DisplayName("자식 경로로 CommentRegisterEvent 반환")
        void testCreate_givenChildPath_willReturnCommentRegisterEvent() {
            // when
            CommentRegisterEvent event = CommentRegisterEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID, TEST_NOTIFICATION_COMMENT_PATH_DEPTH3, TEST_NOTIFICATION_COMMENT_PREVIEW);

            // then
            assertEquals(NotificationActionType.COMMENT_REPLY_ADDED.name(), event.getAction());
            assertEquals(TEST_NOTIFICATION_ACTOR_ID, event.getAuthorId());
            assertEquals(TEST_NOTIFICATION_POST_ULID, event.getPostUlid());
            assertEquals(TEST_NOTIFICATION_COMMENT_PATH_DEPTH3, event.getCommentPath());
            assertEquals(TEST_NOTIFICATION_COMMENT_PREVIEW, event.getContentPreview());
        }


        @Test
        @DisplayName("actorId가 null일 때 예외 반환")
        void testCreate_givenNullActorId_willThrowException() {
            // given & when
            InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                    CommentRegisterEvent.create(null, TEST_NOTIFICATION_POST_ULID, TEST_NOTIFICATION_COMMENT_PATH_DEPTH3, TEST_NOTIFICATION_COMMENT_PREVIEW));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(CommentErrorCode.EMPTY_AUTHOR);
        }

        @Test
        @DisplayName("commentPath가 비어 있을 때 예외 반환")
        void testCreate_givenEmptyCommentPath_willThrowException() {
            // given & when
            InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                    CommentRegisterEvent.create(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_POST_ULID, "", TEST_NOTIFICATION_COMMENT_PREVIEW));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_COMMENT);
        }

    }

}