package kr.modusplant.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import kr.modusplant.infrastructure.security.exception.BusinessAuthenticationException;
import kr.modusplant.infrastructure.security.util.SecurityLogger;
import kr.modusplant.infrastructure.security.util.SecurityResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@RequiredArgsConstructor
public class DefaultAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authEx) throws IOException {

        if (authEx instanceof BusinessAuthenticationException businessAuthEx) {
            SecurityResponseUtils.writeResponse(
                    response, businessAuthEx.getErrorCode().getHttpStatus(),
                    objectMapper.writeValueAsString(DataResponse
                            .of(businessAuthEx.getErrorCode()))
            );
        } else {
            SecurityLogger.logUnknownException(authEx);
            SecurityResponseUtils.writeResponse(
                    response, SecurityErrorCode.AUTHENTICATION_FAILED.getHttpStatus(),
                    objectMapper.writeValueAsString(DataResponse
                            .of(SecurityErrorCode.AUTHENTICATION_FAILED))
            );
        }
    }
}
