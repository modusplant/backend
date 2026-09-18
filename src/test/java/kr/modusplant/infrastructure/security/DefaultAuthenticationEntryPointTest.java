package kr.modusplant.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import kr.modusplant.infrastructure.security.exception.BannedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class DefaultAuthenticationEntryPointTest {

    private final ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
    private final DefaultAuthenticationEntryPoint entryPoint = new DefaultAuthenticationEntryPoint(objectMapper);

    @Test
    @DisplayName("BusinessAuthenticationException 발생 시 해당 에러 코드로 응답 작성 활동 수행")
    void testCommence_givenBusinessAuthenticationException_willWriteBusinessErrorResponse() throws Exception {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        given(response.getWriter()).willReturn(new PrintWriter(stringWriter));
        given(objectMapper.writeValueAsString(any())).willReturn("{\"code\":\"banned\"}");

        // when
        entryPoint.commence(request, response, new BannedException());

        // then
        Mockito.verify(response).setStatus(SecurityErrorCode.BANNED.getHttpStatus());
        assertThat(stringWriter.toString()).isEqualTo("{\"code\":\"banned\"}");
    }

    @Test
    @DisplayName("알 수 없는 인증 예외 발생 시 인증 실패 응답 작성 활동 수행")
    void testCommence_givenUnknownAuthenticationException_willWriteAuthenticationFailedResponse() throws Exception {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        given(response.getWriter()).willReturn(new PrintWriter(stringWriter));
        AuthenticationException exception = new BadCredentialsException("bad credentials");
        given(objectMapper.writeValueAsString(any())).willReturn("{\"code\":\"authentication_failed\"}");

        // when
        entryPoint.commence(request, response, exception);

        // then
        Mockito.verify(response).setStatus(SecurityErrorCode.AUTHENTICATION_FAILED.getHttpStatus());
        assertThat(stringWriter.toString()).isEqualTo("{\"code\":\"authentication_failed\"}");
    }
}
