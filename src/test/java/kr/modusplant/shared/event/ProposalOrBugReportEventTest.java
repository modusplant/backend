package kr.modusplant.shared.event;

import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProposalOrBugReportEventTest {
    @Nested
    @DisplayName("create 테스트")
    class CreateTest {
        @Test
        @DisplayName("memberId가 null일 때 오류 발생")
        void testCreate_givenNullMemberId_willThrowException() {
            // given & when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    ProposalOrBugReportEvent.create(null, TEST_REPORT_TITLE, TEST_REPORT_CONTENT, TEST_REPORT_IMAGE_PATH));

            // then
            assertThat(invalidValueException.getMessage()).contains("NOT_FOUND_MEMBER");
        }

        @Test
        @DisplayName("title이 비어 있을 때 오류 발생")
        void testCreate_givenEmptyTitle_willThrowException() {
            // given & when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    ProposalOrBugReportEvent.create(MEMBER_BASIC_USER_UUID, "", TEST_REPORT_CONTENT, TEST_REPORT_IMAGE_PATH));

            // then
            assertThat(invalidValueException.getMessage()).contains("NOT_FOUND_REPORT");
        }

        @Test
        @DisplayName("content가 비어 있을 때 오류 발생")
        void testCreate_givenEmptyContent_willThrowException() {
            // given & when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    ProposalOrBugReportEvent.create(MEMBER_BASIC_USER_UUID, TEST_REPORT_TITLE, "", TEST_REPORT_IMAGE_PATH));

            // then
            assertThat(invalidValueException.getMessage()).contains("NOT_FOUND_REPORT");
        }
    }
}