package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.event.PostAbuseReportApproveEventTestUtils.testPostAbuseReportApproveEvent;
import static kr.modusplant.shared.exception.enums.GeneralErrorCode.EMPTY_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostAbuseReportApproveEventTest {

    @Nested
    @DisplayName("create 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 파라미터로 PostAbuseReportApproveEvent 반환")
        void testCreate_givenValidParameters_willReturnPostAbuseReportApproveEvent() {
            assertNotNull(testPostAbuseReportApproveEvent);
        }

        @Test
        @DisplayName("postUlid가 비어 있을 때 예외 반환")
        void testCreate_givenBlankPostUlid_willThrowException() {
            // given & when
            EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                    PostAbuseReportApproveEvent.create(""));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EMPTY_VALUE);
        }
    }
}
