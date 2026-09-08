package kr.modusplant.shared.framework.jackson.http.response;

import kr.modusplant.shared.exception.EmptyValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static kr.modusplant.shared.exception.enums.GeneralErrorCode.EMPTY_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataResponseTest {
    @SuppressWarnings("DataFlowIssue")
    @Test
    @DisplayName("메시지가 null일 때 예외 반환")
    void testToString_givenNullMessage_willThrowException() {
        // given
        DataResponse<Void> response = DataResponse.ok();
        ReflectionTestUtils.setField(response, "message", null);

        // when
        EmptyValueException emptyValueException = assertThrows(EmptyValueException.class, response::toString);

        // then
        assertThat(emptyValueException.getErrorCode()).isEqualTo(EMPTY_VALUE);
        assertThat(emptyValueException.getMessage()).contains("[valueName: message]");
    }
}
