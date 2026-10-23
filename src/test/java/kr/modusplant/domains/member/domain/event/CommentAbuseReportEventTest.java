package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.member.common.util.domain.event.CommentAbuseReportEventTestUtils.testCommentAbuseReportEvent;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_CREATED_AT;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static kr.modusplant.shared.exception.enums.GeneralErrorCode.EMPTY_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentAbuseReportEventTest {

    @Nested
    @DisplayName("create 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 파라미터로 CommentAbuseReportEvent 반환")
        void testCreate_givenValidParameters_willReturnCommentAbuseReportEvent() {
            assertNotNull(testCommentAbuseReportEvent);
        }

        @Test
        @DisplayName("postUlid가 비어 있을 때 예외 반환")
        void testCreate_givenBlankPostUlid_willThrowException() {
            // given & when
            EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                    CommentAbuseReportEvent.create("", TEST_COMMENT_PATH, TEST_POST_CREATED_AT));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EMPTY_VALUE);
        }

        @Test
        @DisplayName("path가 비어 있을 때 예외 반환")
        void testCreate_givenBlankPath_willThrowException() {
            // given & when
            EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                    CommentAbuseReportEvent.create(TEST_POST_ULID, " ", TEST_POST_CREATED_AT));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EMPTY_VALUE);
        }

        @Test
        @DisplayName("createdAt이 null일 때 예외 반환")
        void testCreate_givenNullCreatedAt_willThrowException() {
            // given & when
            EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                    CommentAbuseReportEvent.create(TEST_POST_ULID, TEST_COMMENT_PATH, null));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EMPTY_VALUE);
        }
    }
}
