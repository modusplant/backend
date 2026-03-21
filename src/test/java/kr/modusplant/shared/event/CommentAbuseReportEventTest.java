package kr.modusplant.shared.event;

import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentAbuseReportEventTest {
    @Nested
    @DisplayName("create 테스트")
    class CreateTest {
        @Test
        @DisplayName("memberId가 null일 때 오류 발생")
        void testCreate_givenNullMemberId_willThrowException() {
            // given & when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    CommentAbuseReportEvent.create(null, TEST_COMM_POST_ULID, TEST_COMM_COMMENT_PATH));

            // then
            assertThat(invalidValueException.getMessage()).contains("NOT_FOUND_MEMBER");
        }

        @Test
        @DisplayName("postUlid가 비어 있을 때 오류 발생")
        void testCreate_givenEmptyPostUlid_willThrowException() {
            // given & when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    CommentAbuseReportEvent.create(MEMBER_BASIC_USER_UUID, "", TEST_COMM_COMMENT_PATH));

            // then
            assertThat(invalidValueException.getMessage()).contains("NOT_FOUND_POST");
        }

        @Test
        @DisplayName("path가 비어 있을 때 오류 발생")
        void testCreate_givenEmptyPath_willThrowException() {
            // given & when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    CommentAbuseReportEvent.create(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID, ""));

            // then
            assertThat(invalidValueException.getMessage()).contains("NOT_FOUND_COMMENT");
        }
    }
}