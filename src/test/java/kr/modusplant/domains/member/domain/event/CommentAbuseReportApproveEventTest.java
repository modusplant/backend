package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_ADMIN_UUID;
import static kr.modusplant.domains.member.common.util.domain.event.CommentAbuseReportApproveEventTestUtils.testCommentAbuseReportApproveEvent;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static kr.modusplant.shared.exception.enums.GeneralErrorCode.EMPTY_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentAbuseReportApproveEventTest {

    @Nested
    @DisplayName("create 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 파라미터로 CommentAbuseReportApproveEvent 반환")
        void testCreate_givenValidParameters_willReturnCommentAbuseReportApproveEvent() {
            assertNotNull(testCommentAbuseReportApproveEvent);
        }

        @Test
        @DisplayName("postUlid가 비어 있을 때 예외 반환")
        void testCreate_givenBlankPostUlid_willThrowException() {
            // given & when
            EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                    CommentAbuseReportApproveEvent.create("", TEST_COMMENT_PATH, MEMBER_BASIC_ADMIN_UUID));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EMPTY_VALUE);
        }

        @Test
        @DisplayName("path가 비어 있을 때 예외 반환")
        void testCreate_givenBlankPath_willThrowException() {
            // given & when
            EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                    CommentAbuseReportApproveEvent.create(TEST_POST_ULID, " ", MEMBER_BASIC_ADMIN_UUID));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EMPTY_VALUE);
        }

        @Test
        @DisplayName("memberId가 null일 때 예외 반환")
        void testCreate_givenNullMemberId_willThrowException() {
            // given & when
            EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                    CommentAbuseReportApproveEvent.create(TEST_POST_ULID, TEST_COMMENT_PATH, null));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EMPTY_VALUE);
        }
    }
}
