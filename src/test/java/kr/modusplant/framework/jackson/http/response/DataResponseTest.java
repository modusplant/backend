package kr.modusplant.framework.jackson.http.response;

import kr.modusplant.shared.exception.EmptyValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataResponseTest {
    @SuppressWarnings("DataFlowIssue")
    @Test
    @DisplayName("메시지가 null일 때 오류 발생")
    void testToString_givenNullMessage_willThrowException() {
        DataResponse<Void> response = DataResponse.ok();
        ReflectionTestUtils.setField(response, "message", null);
        EmptyValueException emptyValueException = assertThrows(EmptyValueException.class, response::toString);
        assertThat(emptyValueException.getMessage()).contains("[valueName: message]");
    }
}