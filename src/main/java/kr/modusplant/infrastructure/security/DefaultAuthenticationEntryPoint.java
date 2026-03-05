package kr.modusplant.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import kr.modusplant.infrastructure.security.exception.BusinessAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class DefaultAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authEx) throws IOException {

        if (authEx instanceof BusinessAuthenticationException businessAuthEx) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            response.setStatus(businessAuthEx.getErrorCode().getHttpStatus());
            response.getWriter().write(
                    objectMapper.writeValueAsString(DataResponse
                            .of(businessAuthEx.getErrorCode())
                    )
            );
        } else {
            log.error("[Security Error] exceptionName={} | message={}", authEx.getClass(), authEx.getMessage());
            response.setStatus(SecurityErrorCode.AUTHENTICATION_FAILED.getHttpStatus());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(
                    objectMapper.writeValueAsString(DataResponse
                            .of(SecurityErrorCode.AUTHENTICATION_FAILED)
                    )
            );
        }
    }
}
