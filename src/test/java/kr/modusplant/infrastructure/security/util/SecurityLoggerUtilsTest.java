package kr.modusplant.infrastructure.security.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class SecurityLoggerUtilsTest {

    @Test
    @DisplayName("알 수 없는 예외로 로그 기록 활동 수행")
    void testLogUnknownException_givenException_willProcessAction() {
        // given
        RuntimeException exception = new RuntimeException("boom");

        // when & then
        assertThatCode(() -> SecurityLoggerUtils.logUnknownException(exception)).doesNotThrowAnyException();
    }
}
