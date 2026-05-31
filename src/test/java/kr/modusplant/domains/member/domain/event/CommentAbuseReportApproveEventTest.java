package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CommentAbuseReportApproveEventTest {

    @Nested
    @DisplayName("create 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 파라미터로 객체 생성 성공")
        void testCreate_givenValidParameters_willReturnEvent() {
            // when
            CommentAbuseReportApproveEvent event =
                    CommentAbuseReportApproveEvent.create(TEST_POST_ULID, TEST_COMM_COMMENT_PATH);

            // then
            assertNotNull(event);
            assertEquals(TEST_POST_ULID, event.getPostUlid());
            assertEquals(TEST_COMM_COMMENT_PATH, event.getPath());
        }

        @Test
        @DisplayName("postUlid가 비어 있을 때 오류 발생")
        void testCreate_givenBlankPostUlid_willThrowException() {
            // given & when
            InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                    CommentAbuseReportApproveEvent.create("", TEST_COMM_COMMENT_PATH));

            // then
            assertThat(exception.getMessage()).contains("NOT_FOUND_COMMENT");
        }

        @Test
        @DisplayName("path가 비어 있을 때 오류 발생")
        void testCreate_givenBlankPath_willThrowException() {
            // given & when
            InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                    CommentAbuseReportApproveEvent.create(TEST_POST_ULID, " "));

            // then
            assertThat(exception.getMessage()).contains("NOT_FOUND_COMMENT");
        }
    }
}
