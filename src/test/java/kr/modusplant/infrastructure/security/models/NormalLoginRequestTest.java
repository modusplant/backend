package kr.modusplant.infrastructure.security.models;

import kr.modusplant.infrastructure.security.common.util.NormalLoginRequestTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NormalLoginRequestTest implements NormalLoginRequestTestUtils {

    @Test
    @DisplayName("이메일과 비밀번호가 있는 요청으로 String 반환")
    void testToString_givenEmailAndPassword_willReturnString() {
        // given & when
        String result = testLoginRequest.toString();

        // then
        assertThat(result)
                .contains("email=a***@gmail.com", "password=***")
                .doesNotContain(testLoginRequest.email(), testLoginRequest.password());
    }
}
