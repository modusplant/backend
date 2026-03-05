package kr.modusplant.infrastructure.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import kr.modusplant.infrastructure.security.exception.BusinessAuthenticationException;
import kr.modusplant.infrastructure.security.util.SecurityResponseHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class WriteResponseLoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        if(exception instanceof BusinessAuthenticationException ex) {
            SecurityResponseHelper.writeResponse(
                    response, ex.getErrorCode().getHttpStatus(),
                    objectMapper.writeValueAsString(DataResponse
                            .of(ex.getErrorCode()))
            );
        } else {
            SecurityResponseHelper.logUnknownException(exception);
            SecurityResponseHelper.writeResponse(
                    response, SecurityErrorCode.AUTHENTICATION_FAILED.getHttpStatus(),
                    objectMapper.writeValueAsString(DataResponse
                            .of(SecurityErrorCode.AUTHENTICATION_FAILED))
            );
        }
    }
}
