package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.event.PostAbuseReportEventTestUtils.testPostAbuseReportEvent;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_PUBLISHED_AT;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static kr.modusplant.shared.exception.enums.GeneralErrorCode.EMPTY_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostAbuseReportEventTest {

    @Nested
    @DisplayName("create 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 파라미터로 PostAbuseReportEvent 반환")
        void testCreate_givenValidParameters_willReturnPostAbuseReportEvent() {
            assertNotNull(testPostAbuseReportEvent);
        }

        @Test
        @DisplayName("postUlid가 비어 있을 때 예외 반환")
        void testCreate_givenBlankPostUlid_willThrowException() {
            // given & when
            EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                    PostAbuseReportEvent.create("", TEST_POST_PUBLISHED_AT));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EMPTY_VALUE);
        }

        @Test
        @DisplayName("createdAt이 null일 때 예외 반환")
        void testCreate_givenNullCreatedAt_willThrowException() {
            // given & when
            EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                    PostAbuseReportEvent.create(TEST_POST_ULID, null));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EMPTY_VALUE);
        }
    }
}
