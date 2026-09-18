package kr.modusplant.infrastructure.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class DefaultAccessDeniedHandlerTest {

    private final ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
    private final DefaultAccessDeniedHandler handler = new DefaultAccessDeniedHandler(objectMapper);

    @Test
    @DisplayName("접근 거부 예외 발생 시 응답 작성 활동 수행")
    void testHandle_givenAccessDeniedException_willWriteAccessDeniedResponse() throws Exception {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        given(response.getWriter()).willReturn(new PrintWriter(stringWriter));
        given(objectMapper.writeValueAsString(any())).willReturn("{\"code\":\"access_denied\"}");

        // when
        handler.handle(request, response, new AccessDeniedException("denied"));

        // then
        Mockito.verify(response).setStatus(SecurityErrorCode.ACCESS_DENIED.getHttpStatus());
        assertThat(stringWriter.toString()).isEqualTo("{\"code\":\"access_denied\"}");
    }
}
