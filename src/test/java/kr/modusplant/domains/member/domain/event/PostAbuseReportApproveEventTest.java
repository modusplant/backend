package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.event.PostAbuseReportApproveEventTestUtils.testPostAbuseReportApproveEvent;
import static kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode.NOT_FOUND_POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostAbuseReportApproveEventTest {

    @Nested
    @DisplayName("create 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 파라미터로 객체 생성 성공")
        void testCreate_givenValidParameters_willReturnEvent() {
            assertNotNull(testPostAbuseReportApproveEvent);
        }

        @Test
        @DisplayName("postUlid가 비어 있을 때 오류 발생")
        void testCreate_givenBlankPostUlid_willThrowException() {
            // given & when
            InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                    PostAbuseReportApproveEvent.create(""));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(NOT_FOUND_POST);
        }
    }
}
